import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try{
            if(msg instanceof CommandMessage){
                CommandMessage commandMessage = (CommandMessage) msg;
                if(Files.exists(Paths.get("server_storage/" + commandMessage.getFileName()))) {
                    CommandType commandType = commandMessage.getCommandType();
                    switch (commandType) {
                        case DOWNLOAD:
                            FileMessage fileMessage = new FileMessage(Paths.get("server_storage/" + commandMessage.getFileName()));
                            ctx.writeAndFlush(fileMessage);
                            break;
                        case DELETE:
                            Files.delete(Paths.get("server_storage/" + commandMessage.getFileName()));
                            break;
                    }
                }
            }
            if(msg instanceof FileMessage){
                FileMessage fileMessage = (FileMessage) msg;
                Files.write(Paths.get("server_storage/" + fileMessage.getFileName()), fileMessage.getData(), StandardOpenOption.CREATE);
            }
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
