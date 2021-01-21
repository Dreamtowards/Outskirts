package outskirts.client.gui.inspection.mtl;

import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiPadding;
import outskirts.client.gui.GuiText;
import outskirts.client.gui.GuiExpander;
import outskirts.client.gui.inspection.num.GuiIScalar;
import outskirts.client.gui.stat.GuiColumn;
import outskirts.client.gui.stat.GuiRow;
import outskirts.client.render.renderer.preferences.RenderPerferences;

import java.util.function.Consumer;

public class GuiIMaterial extends Gui {

    public GuiIMaterial(RenderPerferences renderPerferences) {

        Consumer<GuiText> titleprop = g -> {g.setWidth(140);};

        addChildren(
          new GuiPadding(Insets.fromLTRB(8,0,0,0)).setContent(new GuiColumn().addChildren(
            new GuiExpander("DiffuseMap").setContent(new GuiRow().addChildren(
              new GuiText("DiffuseMap").exec(titleprop),
              new GuiITexture(renderPerferences::getDiffuseMap, renderPerferences::setDiffuseMap)
            )),
            new GuiExpander("EmissionMap").setContent(new GuiRow().addChildren(
              new GuiText("EmissionMap").exec(titleprop),
              new GuiITexture(renderPerferences::getEmissionMap, renderPerferences::setEmissionMap)
            )),
            new GuiExpander("NormalMap").setContent(new GuiRow().addChildren(
              new GuiText("NormalMap").exec(titleprop),
              new GuiITexture(renderPerferences::getNormalMap, renderPerferences::setNormalMap)
            )),
            new GuiExpander("Specular").setContent(new GuiColumn().addChildren(
              new GuiRow().addChildren(
                new GuiText("SpecularStrength").exec(titleprop),
                new GuiIScalar(renderPerferences::getSpecularStrength, renderPerferences::setSpecularStrength)
              ),
              new GuiRow().addChildren(
                new GuiText("Shininess").exec(titleprop),
                new GuiIScalar(renderPerferences::getShininess, renderPerferences::setShininess)
              ),
              new GuiRow().addChildren(
                new GuiText("SpecularMap").exec(titleprop),
                new GuiITexture(renderPerferences::getSpecularMap, renderPerferences::setSpecularMap)
              )
            )),
            new GuiExpander("Displacement").setContent(new GuiColumn().addChildren(
              new GuiRow().addChildren(
                new GuiText("DisplacementScale").exec(titleprop),
                new GuiIScalar(renderPerferences::getDisplacementScale, renderPerferences::setDisplacementScale)
              ),
              new GuiRow().addChildren(
                new GuiText("DisplacementMap").exec(titleprop),
                new GuiITexture(renderPerferences::getDisplacementMap, renderPerferences::setDisplacementMap)
              )
            ))
          ))
        );
    }


}
