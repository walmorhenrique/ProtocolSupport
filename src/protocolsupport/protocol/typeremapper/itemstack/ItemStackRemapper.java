package protocolsupport.protocol.typeremapper.itemstack;

import protocolsupport.api.ProtocolType;
import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.typeremapper.itemstack.complex.ItemStackComplexRemapperRegistry;
import protocolsupport.protocol.typeremapper.pe.PEItems;
import protocolsupport.protocol.utils.CommonNBT;
import protocolsupport.protocol.utils.types.NetworkItemStack;
import protocolsupport.protocol.utils.types.nbt.NBTCompound;
import protocolsupport.protocol.utils.types.nbt.NBTShort;

public class ItemStackRemapper {

	public static NetworkItemStack remapToClient(ProtocolVersion version, String locale,  NetworkItemStack itemstack) {
		setComplexlyRemapped(itemstack, false);
		itemstack = ItemStackComplexRemapperRegistry.remapToClient(version, locale, itemstack);
		itemstack.setTypeId(LegacyItemType.REGISTRY.getTable(version).getRemap(itemstack.getTypeId()));
		if (version.getProtocolType() == ProtocolType.PE) {
			if (PEItems.hasDurability(itemstack.getTypeId())) {
				NBTCompound nbt = itemstack.getNBT();
				if (nbt == null) {
					nbt = new NBTCompound();
				}
				System.out.println(nbt.toString());
				nbt.setTag(CommonNBT.DAMAGE, new NBTShort((short) itemstack.getLegacyData()));
				itemstack.setNBT(nbt);
			}
			int peCombinedId = PEItems.getPECombinedIdByModernId(itemstack.getTypeId());
			itemstack.setTypeId(PEItems.getIdFromPECombinedId(peCombinedId));
			itemstack.setLegacyData(PEItems.getDataFromPECombinedId(peCombinedId));
		} else if (version.isBefore(ProtocolVersion.MINECRAFT_1_13)) {
			int legacyCombinedId = PreFlatteningItemIdData.getLegacyCombinedIdByModernId(itemstack.getTypeId());
			itemstack.setTypeId(PreFlatteningItemIdData.getIdFromLegacyCombinedId(legacyCombinedId));
			if (!isComplexlyRemapped(itemstack)) {
				itemstack.setLegacyData(PreFlatteningItemIdData.getDataFromLegacyCombinedId(legacyCombinedId));
			}
		} else {
			itemstack.setTypeId(FlatteningItemId.REGISTRY_TO_CLIENT.getTable(version).getRemap(itemstack.getTypeId()));
		}
		return itemstack;
	}

	public static NetworkItemStack remapFromClient(ProtocolVersion version, String locale, NetworkItemStack itemstack) {
		if (version.getProtocolType() == ProtocolType.PE) {
			int modernId = PEItems.getModernIdByPEIdData(itemstack.getTypeId(), itemstack.getLegacyData());
			itemstack.setTypeId(modernId);
			itemstack.setLegacyData(0);
		} else if (version.isBefore(ProtocolVersion.MINECRAFT_1_13)) {
			int legacyCombinedId = PreFlatteningItemIdData.formLegacyCombinedId(itemstack.getTypeId(), itemstack.getLegacyData());
			itemstack.setTypeId(PreFlatteningItemIdData.getModernIdByLegacyCombinedId(legacyCombinedId));
		} else {
			itemstack.setTypeId(FlatteningItemId.REGISTRY_FROM_CLIENT.getTable(version).getRemap(itemstack.getTypeId()));
		}
		return ItemStackComplexRemapperRegistry.remapFromClient(version, locale, itemstack);
	}

	public static void setComplexlyRemapped(NetworkItemStack itemstack, boolean remapped) {
		itemstack.setLegacyData(remapped ? 0 : -1);
	}

	public static boolean isComplexlyRemapped(NetworkItemStack itemstack) {
		return itemstack.getLegacyData() != -1;
	}

}
