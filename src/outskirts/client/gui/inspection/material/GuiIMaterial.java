package outskirts.client.gui.inspection.material;

import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiPadding;
import outskirts.client.gui.GuiText;
import outskirts.client.gui.GuiExpander;
import outskirts.client.gui.inspection.num.GuiIScalar;
import outskirts.client.gui.GuiColumn;
import outskirts.client.gui.GuiRow;
import outskirts.client.material.Material;

import java.util.function.Consumer;

public class GuiIMaterial extends Gui {

    public GuiIMaterial(Material material) {

        Consumer<GuiText> titleprop = g -> {g.setWidth(140);};

        addChildren(
          new GuiPadding(Insets.fromLTRB(8,0,0,0)).setContent(new GuiColumn().addChildren(
            new GuiExpander("DiffuseMap").setContent(new GuiRow().addChildren(
              new GuiText("DiffuseMap").exec(titleprop),
              new GuiITexture(material::getDiffuseMap, material::setDiffuseMap)
            )),
            new GuiExpander("EmissionMap").setContent(new GuiRow().addChildren(
              new GuiText("EmissionMap").exec(titleprop),
              new GuiITexture(material::getEmissionMap, material::setEmissionMap)
            )),
            new GuiExpander("NormalMap").setContent(new GuiRow().addChildren(
              new GuiText("NormalMap").exec(titleprop),
              new GuiITexture(material::getNormalMap, material::setNormalMap)
            )),
            new GuiExpander("Specular").setContent(new GuiColumn().addChildren(
              new GuiRow().addChildren(
                new GuiText("SpecularStrength").exec(titleprop),
                new GuiIScalar(material::getSpecularStrength, material::setSpecularStrength)
              ),
              new GuiRow().addChildren(
                new GuiText("Shininess").exec(titleprop),
                new GuiIScalar(material::getShininess, material::setShininess)
              ),
              new GuiRow().addChildren(
                new GuiText("SpecularMap").exec(titleprop),
                new GuiITexture(material::getSpecularMap, material::setSpecularMap)
              )
            )),
            new GuiExpander("Displacement").setContent(new GuiColumn().addChildren(
              new GuiRow().addChildren(
                new GuiText("DisplacementScale").exec(titleprop),
                new GuiIScalar(material::getDisplacementScale, material::setDisplacementScale)
              ),
              new GuiRow().addChildren(
                new GuiText("DisplacementMap").exec(titleprop),
                new GuiITexture(material::getDisplacementMap, material::setDisplacementMap)
              )
            ))
          ))
        );
    }


}
