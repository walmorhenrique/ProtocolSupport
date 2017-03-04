package protocolsupport.protocol.packet.middle.clientbound.play;

import protocolsupport.protocol.packet.middle.ClientBoundMiddlePacket;
import protocolsupport.protocol.serializer.ProtocolSupportPacketDataSerializer;

public abstract class MiddleChat extends ClientBoundMiddlePacket {

	protected String chatJson;
	protected byte position;

	@Override
	public void readFromServerData(ProtocolSupportPacketDataSerializer serializer) {
		chatJson = serializer.readString();
		position = serializer.readByte();
	}

}
