package codec;

import chatBean.ProtoInstant;
import Exception.InvalidFrameException;
import chatBean.msg.ProtoMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ProtobufDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //标记当前的readIndex位置
        in.markReaderIndex();
        //判断包头长度
        if (in.readableBytes() < 8) {
            return;
        }
        //读取魔数
        short magic = in.readShort();
        if (magic != ProtoInstant.MAGIC_CODE) {
            String error = "客户端口令不对:" + ctx.channel().remoteAddress();
            throw new InvalidFrameException(error);
        }

        //读取版本
        short version = in.readShort();
        //读取长度
        int length = in.readInt();

        if (length < 0) {
            ctx.close();
        }

        if (length > in.readableBytes()) {
            //重置读取位置
            in.resetReaderIndex();
            return;
        }

        byte[] array;
        if (in.hasArray()) {
            //堆缓存
            ByteBuf slice = in.slice(in.readerIndex(), length);
            array = slice.array();
            in.retain();
        } else {
            //直接缓存
            array = new byte[length];
            in.readBytes(array, 0, length);
        }

        ProtoMsg.Message outmsg = ProtoMsg.Message.parseFrom(array);

        if (in.hasArray()) {
            in.release();
        }

        if (outmsg != null) {
            out.add(outmsg);
        }

    }
}
