package simpleHttpServer.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simpleHttpServer.annotation.SimpleController;
import simpleHttpServer.annotation.SimpleRequestMapping;
import simpleHttpServer.exception.SimpleHttpServerException;
import simpleHttpServer.handler.ControllerHandler;
import simpleHttpServer.reflect.ClassUtil;
import simpleHttpServer.reflect.Singleton;
import simpleHttpServer.utils.ConfigSetting;

import java.lang.reflect.Method;
import java.util.Set;

public class SeverStart {
    Logger logger = LoggerFactory.getLogger(SeverStart.class);
    public void start (int port) throws Exception {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        try {
            ServerBootstrap server = new ServerBootstrap();
            server.group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new HttpObjectAggregator(1024 * 1024));
                            pipeline.addLast(new ControllerHandler());
                        }
                    });
            init();
            bind(server,port);
        } finally {
            boss.shutdownGracefully().sync();
            work.shutdownGracefully().sync();
        }


    }
    private ChannelFuture bind (ServerBootstrap server,int port) throws Exception {
        ChannelFuture channelFuture = server.bind(port).addListener(t-> {
            if (t.isSuccess()) {
                logger.info("绑定端口成功,port"+port);
            } else {
                logger.info("绑定端口失败");
                throw  new SimpleHttpServerException("启动失败,端口:"+port,t.cause());
            }
        });
        channelFuture.channel().closeFuture().sync();
        return channelFuture;
    }
//    初始化Mapping,以及实例化controller
    private void init () throws Exception {
        if (ConfigSetting.getScanPackageURL() == null) {
            throw new SimpleHttpServerException("please set scanner package URL");
        }
        Set<Class<?>> simpleHttpServer = ClassUtil.getClassSet(ConfigSetting.getScanPackageURL());
        for (Class<?> cl : simpleHttpServer) {
            StringBuffer path = new StringBuffer().append(ConfigSetting.getRootPath());
            String className = cl.getName();
            Class<?> aClass = Class.forName(className);
            if (null  != aClass.getAnnotation(SimpleController.class)) {
    //          实例化controller
                Singleton.get(aClass);
                SimpleRequestMapping annotation = aClass.getAnnotation(SimpleRequestMapping.class);
                if (null !=  annotation) {
                    path.append(annotation.value());
                }
                Method[] methods = aClass.getMethods();
                for (Method m : methods) {
                    String methodName = m.getName();
                    StringBuffer path1 = new StringBuffer().append(path);
                    SimpleRequestMapping annotation1 = m.getAnnotation(SimpleRequestMapping.class);
                    if (null != annotation1) {
                        path1.append(annotation1.value());
                        if (ConfigSetting.containsKey(path1.toString())) {
                            throw new SimpleHttpServerException("map "+path+" is exists");
                        } else {
                            ConfigSetting.put(path1.toString(),className+"#"+methodName);
                            logger.info("class-method:"+className+"#"+methodName);
                        }
                    }
                }
            }

        }
    }
}
