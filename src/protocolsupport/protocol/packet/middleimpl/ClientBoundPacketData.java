package protocolsupport.protocol.packet.middleimpl;

import io.netty.util.Recycler;
import io.netty.util.Recycler.Handle;
import protocolsupport.protocol.packet.PacketData;
import protocolsupport.protocol.packet.PacketType;

public class ClientBoundPacketData extends PacketData<ClientBoundPacketData> {

	public static ClientBoundPacketData create(PacketType packetType) {
		return recycler.get().init(packetType);
	}

	protected static final Recycler<ClientBoundPacketData> recycler = new Recycler<ClientBoundPacketData>() {
		@Override
		protected ClientBoundPacketData newObject(Handle<ClientBoundPacketData> handle) {
			return new ClientBoundPacketData(handle);
		}
	};

	protected ClientBoundPacketData(Handle<ClientBoundPacketData> handle) {
		super(handle);
	}

	@Override
	public ClientBoundPacketData clone() {
		ClientBoundPacketData clone = recycler.get().init(packetType);
		getBytes(readerIndex(), clone);
		return clone;
	}

}
