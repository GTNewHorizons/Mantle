package slimeknights.mantle.client.gui.book.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.stats.Achievement;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

import slimeknights.mantle.client.book.data.SectionData;

public class ElementSection extends SizedBookElement {

  public static final int IMG_SIZE = 32;

  public static final int WIDTH = 42;
  public static final int HEIGHT = 42;

  private SectionData section;

  public ElementSection(int x, int y, SectionData section) {
    super(x, y, WIDTH, HEIGHT);

    this.section = section;
  }

  @Override
  public void draw(int mouseX, int mouseY, float partialTicks, FontRenderer fontRenderer) {
    boolean unlocked = section.isUnlocked(parent.statFile);
    boolean hover = isHovered(mouseX, mouseY);

    if(section.icon != null) {
      if(unlocked) {
        GlStateManager.color(1F, 1F, 1F, hover ? 1F : 0.5F);
      } else {
        float r = ((parent.book.appearance.lockedSectionColor >> 16) & 0xff) / 255.F;
        float g = ((parent.book.appearance.lockedSectionColor >> 8) & 0xff) / 255.F;
        float b = (parent.book.appearance.lockedSectionColor & 0xff) / 255.F;
        GlStateManager.color(r, g, b, 0.75F);
      }

      if(section.icon.item == null) {
        if(section.icon.location != null) {
          renderEngine.bindTexture(section.icon.location);

          drawScaledCustomSizeModalRect(x + WIDTH / 2 - IMG_SIZE / 2, y + HEIGHT / 2 - IMG_SIZE / 2, section.icon.u, section.icon.v, section.icon.uw, section.icon.vh, IMG_SIZE, IMG_SIZE, section.icon.texWidth, section.icon.texHeight);
        }
      } else {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + WIDTH / 2 - IMG_SIZE / 2, y + HEIGHT / 2 - IMG_SIZE / 2, 0);
        GlStateManager.scale(2F, 2F, 1F);
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(section.icon.item.getItems()[0], 0, 0);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
      }
    }

    if(section.parent.appearance.drawSectionListText) {
      fontRenderer.drawString(section.getTitle(),
                              x + WIDTH / 2 - fontRenderer.getStringWidth(section.getTitle()) / 2,
                              y + HEIGHT - fontRenderer.FONT_HEIGHT/2,
                              hover ? 0xFF000000 : 0x7F000000);
    }
  }

  @Override
  public void drawOverlay(int mouseX, int mouseY, float partialTicks, FontRenderer fontRenderer) {
    if(section != null && isHovered(mouseX, mouseY)) {
      List<String> text = new ArrayList<>();
      text.add(section.getTitle());
      if(!section.isUnlocked(parent.statFile)) {
        text.add(TextFormatting.RED + "Locked");
        text.add("Requirements:");

        for(String requirement : section.requirements) {
          Achievement achievement = SectionData.findAchievement(requirement);
          if(achievement != null) {
            text.add((SectionData
                          .requirementSatisfied(requirement, parent.statFile) ? TextFormatting.GREEN : TextFormatting.RED) + TextFormatting
                         .getTextWithoutFormattingCodes(achievement.getStatName().getFormattedText()));
          }
        }
      }
      drawHoveringText(text, mouseX, mouseY, Minecraft.getMinecraft().fontRendererObj);
    }
  }

  @Override
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if(mouseButton == 0 && section != null && section.isUnlocked(parent.statFile) && isHovered(mouseX, mouseY)) {
      parent.openPage(parent.book.getFirstPageNumber(section, parent.statFile));
    }
  }
}