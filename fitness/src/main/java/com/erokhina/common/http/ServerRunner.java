package com.erokhina.common.http;

import com.erokhina.manager.http.HttpMappingHandler;
import com.github.vanbv.num.AbstractHttpMappingHandler;
import com.github.vanbv.num.json.JsonParserDefault;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerKeepAliveHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class ServerRunner {
    public static void runServer(int port, AbstractHttpMappingHandler handler) throws Exception {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast("HttpServerCodec", new HttpServerCodec())
                                    .addLast("HttpServerKeepAlive", new HttpServerKeepAliveHandler())
                                    .addLast("HttpObjectAggregator", new HttpObjectAggregator(10 * 1024 * 102, true))
                                    .addLast("HttpChunkedWrite", new ChunkedWriteHandler())
                                    .addLast("HttpMappingHandler", handler);
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
