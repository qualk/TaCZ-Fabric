package com.tacz.guns.client.input;

import com.tacz.guns.api.client.event.InputEvent;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.api.item.IGun;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import static com.tacz.guns.util.InputExtraCheck.isInGame;

@Environment(EnvType.CLIENT)
public class FireSelectKey {
    public static final KeyBinding FIRE_SELECT_KEY = new KeyBinding("key.tacz.fire_select.desc",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.category.tacz");

    public static void onFireSelectKeyPress(InputEvent.Key event) {
        if (isInGame() && event.getAction() == GLFW.GLFW_PRESS && FIRE_SELECT_KEY.matchesKey(event.getKey(), event.getScanCode())) {
            doFireSelectLogic();
        }
    }

    public static void onFireSelectMousePress(InputEvent.MouseButton.Post event) {
        if (isInGame() && event.getAction() == GLFW.GLFW_PRESS && FIRE_SELECT_KEY.matchesMouse(event.getButton())) {
            doFireSelectLogic();
        }
    }

    private static void doFireSelectLogic() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null || player.isSpectator()) {
            return;
        }
        if (IGun.mainhandHoldGun(player)) {
            IClientPlayerGunOperator.fromLocalPlayer(player).fireSelect();
        }
    }
}
