package com.tacz.guns.compat.jei.entry;

import com.google.common.collect.Lists;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.GunTabType;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.builder.AttachmentItemBuilder;
import com.tacz.guns.api.item.builder.GunItemBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Locale;

public class AttachmentQueryEntry {
    public static final int MAX_GUN_SHOW_COUNT = 60;

    private final Identifier id;
    private final GunTabType type;

    /**
     * 显示的配件
     */
    private final ItemStack attachmentStack;
    /**
     * 用于能显示的枪械
     */
    private List<ItemStack> allowGunStacks;
    /**
     * 如果是显示不下的，那么全放这里
     */
    private List<ItemStack> extraAllowGunStacks;

    public AttachmentQueryEntry(Identifier attachmentId, GunTabType type) {
        this.id = attachmentId;
        this.type = type;
        this.attachmentStack = AttachmentItemBuilder.create().setId(attachmentId).build();
        this.allowGunStacks = Lists.newArrayList();
        this.extraAllowGunStacks = Lists.newArrayList();
        this.addAllAllowGuns();
        this.dividedGuns();
    }

    public static List<AttachmentQueryEntry> getAllAttachmentQueryEntries() {
        List<AttachmentQueryEntry> entries = Lists.newArrayList();
        TimelessAPI.getAllCommonAttachmentIndex().forEach(entry -> {
            for (GunTabType tabType : GunTabType.values()) {
                AttachmentQueryEntry queryEntry = new AttachmentQueryEntry(entry.getKey(), tabType);
                if (!queryEntry.getAllowGunStacks().isEmpty()) {
                    entries.add(queryEntry);
                }
            }
        });
        return entries;
    }

    public Identifier getId() {
        return id;
    }

    public GunTabType getType() {
        return type;
    }

    public ItemStack getAttachmentStack() {
        return attachmentStack;
    }

    public List<ItemStack> getAllowGunStacks() {
        return allowGunStacks;
    }

    public List<ItemStack> getExtraAllowGunStacks() {
        return extraAllowGunStacks;
    }

    private void addAllAllowGuns() {
        TimelessAPI.getAllCommonGunIndex().forEach(entry -> {
            String tabType = type.name().toLowerCase(Locale.US);
            String gunType = entry.getValue().getType();
            if (tabType.equals(gunType)) {
                ItemStack gun = GunItemBuilder.create().setId(entry.getKey()).build();
                if (!(gun.getItem() instanceof IGun iGun)) {
                    return;
                }
                if (iGun.allowAttachment(gun, this.attachmentStack)) {
                    this.allowGunStacks.add(gun);
                }
            }
        });
    }

    private void dividedGuns() {
        int size = this.allowGunStacks.size();
        if (size >= MAX_GUN_SHOW_COUNT) {
            this.extraAllowGunStacks = this.allowGunStacks.subList(MAX_GUN_SHOW_COUNT, size);
            this.allowGunStacks = this.allowGunStacks.subList(0, MAX_GUN_SHOW_COUNT);
        }
    }
}
