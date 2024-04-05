package com.programmerlexi.pl;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

public class LinuxPlayerctlClient implements ClientModInitializer {
  public static void doCommand(String cmd, MinecraftClient c) {
    if (!System.getProperty("os.name").equals("Linux")) {
      c.player.sendMessage(Text.literal("Only Linux is currently supported. Yours is:"), false);
      c.player.sendMessage(Text.literal(System.getProperty("os.name")), false);
      c.player.sendMessage(Text.literal("Should you think this is a bug please report it."));
      return;
    }
    try {
      Runtime.getRuntime().exec(cmd);
    } catch (IOException e) {
      System.err.println(e);
    }
  }

  @Override
  public void onInitializeClient() {
    KeyBinding prev = KeyBindingHelper.registerKeyBinding(
        new KeyBinding("key.playerctl.prev", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, "key.category.playerctl"));
    KeyBinding next = KeyBindingHelper.registerKeyBinding(
        new KeyBinding("key.playerctl.next", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "key.category.playerctl"));
    KeyBinding playPause = KeyBindingHelper
        .registerKeyBinding(new KeyBinding("key.playerctl.play", GLFW.GLFW_KEY_J, "key.category.playerctl"));
    ClientTickEvents.END_CLIENT_TICK.register(client -> {
      while (prev.wasPressed()) {
        client.player.sendMessage(Text.literal("Playing previous song"), false);
        doCommand("playerctl previous", client);
      }

      while (next.wasPressed()) {
        client.player.sendMessage(Text.literal("Playing next song"), false);
        doCommand("playerctl next", client);
      }

      if (playPause.wasPressed()) {
        client.player.sendMessage(Text.literal("Toggling playback"), false);
        doCommand("playerctl play-pause", client);
      }
    });
  }
}
