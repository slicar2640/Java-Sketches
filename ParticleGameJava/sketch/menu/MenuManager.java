package sketch.menu;

import java.awt.BasicStroke;
import java.util.HashMap;

import sketch.DrawManager;
import sketch.ParticleGameJava;
import sketch.construct.ConstructBuilder;
import sketch.menu.item.*;
import sketch.mouse.*;
import sketch.particle.*;
import sketch.stick.*;
import sketch.util.MathUtils;

public class MenuManager {
  public boolean active = false;
  private Menu currentMenu;
  private HashMap<String, Menu> menuMap;
  public boolean justExitedMenu = false;
  public ParticleGameJava sketch;

  public MenuManager(ParticleGameJava sketch) {
    this.sketch = sketch;
    menuMap = new HashMap<>();
    menuMap.put("StartOfAdd", new Menu("StartOfAdd", this));
    menuMap.put("CustomConstruct", new Menu("CustomConstruct", this));
    menuMap.put("AddStick", new Menu("AddStick", this));
    initStartOfAdd(menuMap.get("StartOfAdd"));
    initCustomConstruct(menuMap.get("CustomConstruct"));
    initAddStick(menuMap.get("AddStick"));
  }

  public void open(String menuName) {
    active = true;
    if (!menuMap.containsKey(menuName)) {
      throw new IllegalArgumentException("Menu " + menuName + " does not exist");
    }
    currentMenu = menuMap.get(menuName);
  }

  public void click() {
    currentMenu.click();
  }

  public void exit() {
    justExitedMenu = true;
    active = false;
  }

  public void show(DrawManager dm) {
    currentMenu.show(dm);
  }

  private void initStartOfAdd(Menu menu) {
    menu.addItem(new Button("p-normal", 50, 50, 120, 50, "Particle", menu, (button) -> {
      sketch.mouseManager.currentInteraction = new AddParticleInteraction(Particle.class, 100, 1, sketch);
    }, (x, y, w, dm) -> {
      dm.fill(255);
      dm.noStroke();
      dm.circle(x + w / 2, y + w / 2, w * 0.3f);
    }));
    menu.addItem(new Button("p-sticky", 50, 110, 170, 50, "StickyParticle", menu, (button) -> {
      sketch.mouseManager.currentInteraction = new AddParticleInteraction(StickyParticle.class, 100, 1, sketch);
    }, (x, y, w, dm) -> {
      dm.fill(255, 255, 180);
      dm.noStroke();
      dm.circle(x + w / 2, y + w / 2, w * 0.3f);
    }));
    menu.addItem(new Button("p-linkable", 50, 170, 190, 50, "LinkableParticle", menu, button -> {
      sketch.mouseManager.currentInteraction = new AddParticleInteraction(LinkableParticle.class, 100, 1, sketch);
    }, (x, y, w, dm) -> {
      dm.fill(255, 0, 0);
      dm.noStroke();
      dm.circle(x + w / 2, y + w / 2, w * 0.3f);
    }));
    menu.addItem(new Button("p-plant", 50, 230, 190, 50, "PlantParticle", menu, button -> {
      sketch.mouseManager.currentInteraction = new AddParticleInteraction(PlantParticle.class, 100, 1, sketch);
    }, (x, y, w, dm) -> {
      dm.fill(0, 185, 30);
      dm.noStroke();
      dm.circle(x + w / 2, y + w / 2, w * 0.3f);
    }));

    menu.addItem(new LinkButton("custom", 300, 50, 120, 50, "Custom", "CustomConstruct", menu, button -> {
    }, (x, y, w, dm) -> {
      dm.stroke(255);
      dm.strokeWeight(4);
      dm.noFill();
      dm.circle(x + w / 2, y + w / 2, w * 0.225f);
      for (int i = 0; i < 8; i++) {
        float a = (float) ((float) i / 8 * 2 * Math.PI);
        dm.line(x + w * (float) (0.5 + 0.25 * Math.cos(a)), y + w * (float) (0.5 + 0.25 * Math.sin(a)),
            x + w * (float) (0.5 + 0.35 * Math.cos(a)), y + w * (float) (0.5 + 0.35 * Math.sin(a)));
      }
    }));

    menu.addItem(new Button("c-loop", 300, 110, 100, 50, "Loop", menu, button -> {
      sketch.mouseManager.currentInteraction = new AddConstructInteraction(
          (scale, count) -> sketch.constructBuilder.addLoop(sketch.mouseX, sketch.mouseY, scale, count), 100, 150,
          ConstructBuilder.drawLoop, sketch);
    }, (x, y, w, dm) -> {
      dm.stroke(255);
      dm.strokeWeight(2);
      dm.noFill();
      dm.circle(x + w / 2, y + w / 2, w * 0.3f);
    }));
    menu.addItem(new Button("c-triangle", 300, 170, 125, 50, "Triangle", menu, button -> {
      sketch.mouseManager.currentInteraction = new AddConstructInteraction(
          (scale, count) -> sketch.constructBuilder.addTriangle(sketch.mouseX, sketch.mouseY, count, scale), 50, 5,
          ConstructBuilder.drawTriangle, sketch);
    }, (x, y, w, dm) -> {
      dm.stroke(255);
      dm.strokeWeight(2);
      dm.noFill();
      dm.triangle(x + w * 0.5f, y + w * 0.2f, x + w * 0.8f, y + w * (0.5f + 0.3f * MathUtils.SQRT3_2), x + w * 0.2f,
          y + w * (0.5f + 0.3f * MathUtils.SQRT3_2));
    }));
    menu.addItem(new Button("c-rectangle", 300, 230, 140, 50, "Rectangle", menu, button -> {
      sketch.mouseManager.currentInteraction = new AddConstructInteraction((scale, count) -> sketch.constructBuilder
          .addRectangle(sketch.mouseX, sketch.mouseY, count, MathUtils.firstOddBefore(count), scale, true), 50, 5,
          ConstructBuilder.drawRectangle, sketch);
    }, (x, y, w, dm) -> {
      dm.stroke(255);
      dm.strokeWeight(2);
      dm.noFill();
      dm.rect(x + w * 0.2f, y + w * 0.2f, w * 0.6f, w * 0.6f);
    }));
    menu.addItem(new Button("c-classicRectangle", 300, 290, 210, 50, "Rectangle (Classic)", menu, button -> {
      sketch.mouseManager.currentInteraction = new AddConstructInteraction((scale, count) -> sketch.constructBuilder
          .addClassicRectangle(sketch.mouseX, sketch.mouseY, count, count, scale, true, true, false), 50, 5,
          ConstructBuilder.drawClassicRectangle, sketch);
    }, (x, y, w, dm) -> {
      dm.stroke(255);
      dm.strokeWeight(2);
      dm.noFill();
      dm.rect(x + w * 0.2f, y + w * 0.2f, w * 0.6f, w * 0.6f);
      dm.line(x + w * 0.21f, y + w * 0.21f, x + w * 0.79f, y + w * 0.79f);
    }));
  }

  private void initCustomConstruct(Menu menu) {
    RadioSelector shapeSelector = new RadioSelector("shape", 50, 50, 150, 30, "Shape", menu);
    menu.addItem(shapeSelector);
    shapeSelector.addButton("loop", "Loop", (x, y, w, dm) -> {
      dm.stroke(255);
      dm.strokeWeight(2);
      dm.noFill();
      dm.circle(x + w / 2, y + w / 2, w * 0.3f);
    });
    shapeSelector.addButton("triangle", "Triangle", (x, y, w, dm) -> {
      dm.stroke(255);
      dm.strokeWeight(2);
      dm.noFill();
      dm.triangle(x + w * 0.5f, y + w * 0.2f, x + w * 0.8f, y + w * (0.5f + 0.3f * MathUtils.SQRT3_2), x + w * 0.2f,
          y + w * (0.5f + 0.3f * MathUtils.SQRT3_2));
    });
    shapeSelector.addButton("rectangle", "Rectangle", (x, y, w, dm) -> {
      dm.stroke(255);
      dm.strokeWeight(2);
      dm.noFill();
      dm.rect(x + w * 0.2f, y + w * 0.2f, w * 0.6f, w * 0.6f);
    });
    shapeSelector.addButton("classicRectangle", "Rectangle (Classic)", (x, y, w, dm) -> {
      dm.stroke(255);
      dm.strokeWeight(2);
      dm.noFill();
      dm.rect(x + w * 0.2f, y + w * 0.2f, w * 0.6f, w * 0.6f);
      dm.line(x + w * 0.21f, y + w * 0.21f, x + w * 0.79f, y + w * 0.79f);
    });
    shapeSelector.addButton("chain", "Chain", (x, y, w, dm) -> {
      dm.stroke(255);
      dm.strokeWeight(2);
      dm.line(x + w * 0.2f, y + w * 0.5f, x + w * 0.4f, y + w * 0.7f);
      dm.line(x + w * 0.4f, y + w * 0.7f, x + w * 0.6f, y + w * 0.3f);
      dm.line(x + w * 0.6f, y + w * 0.3f, x + w * 0.8f, y + w * 0.5f);
    });

    NumericInput countInput = new NumericInput("count", 50, 250, 100, 30, "Count", 5, 1, Integer.MAX_VALUE, menu);
    menu.addItem(countInput);

    shapeSelector.setClickFunction(selector -> {
      String val = selector.value();
      String prevVal = selector.previousValue();
      if (val != null) {
        if (val.equals("loop") && (prevVal == null || !prevVal.equals("loop"))) {
          countInput.value = 100;
        } else if (prevVal != null && prevVal.equals("loop") && val.equals("loop")) {
          countInput.value = 5;
        }
      }
    });

    RadioSelector particleSelector = new RadioSelector("particle", 225, 50, 150, 30, "Particle Type", menu);
    menu.addItem(particleSelector);
    particleSelector.addButton("particle", "Particle", (x, y, w, dm) -> {
      dm.fill(255);
      dm.noStroke();
      dm.circle(x + w / 2, y + w / 2, w * 0.3f);
    });
    particleSelector.addButton("stickyParticle", "StickyParticle", (x, y, w, dm) -> {
      dm.fill(255, 255, 180);
      dm.noStroke();
      dm.circle(x + w / 2, y + w / 2, w * 0.3f);
    });
    particleSelector.addButton("linkableParticle", "LinkableParticle", (x, y, w, dm) -> {
      dm.fill(255, 0, 0);
      dm.noStroke();
      dm.circle(x + w / 2, y + w / 2, w * 0.3f);
    });
    particleSelector.addButton("plantParticle", "PlantParticle", (x, y, w, dm) -> {
      dm.fill(0, 185, 30);
      dm.noStroke();
      dm.circle(x + w / 2, y + w / 2, w * 0.3f);
    });

    menu.addItem(new NumericInput("mass", 250, 210, 100, 30, "Mass", 1, 1, Integer.MAX_VALUE, menu));
    menu.addItem(new ToggleButton("static", 270, 280, 60, "Static", false, menu));

    RadioSelector stickSelector = new RadioSelector("stick", 400, 50, 150, 30, "Stick Type", menu);
    menu.addItem(stickSelector);
    stickSelector.addButton("stick", "Stick", (x, y, w, dm) -> {
      dm.stroke(255);
      dm.strokeWeight(2);
      dm.line(x + w * 0.2f, y + w * 0.2f, x + w * 0.8f, y + w * 0.8f);
    });
    stickSelector.addButton("wallStick", "WallStick", (x, y, w, dm) -> {
      dm.strokeStyle(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
      boolean dark = true;
      for (int i = 0; i < 4; i++) {
        float f = 0.2f + (float) i * 0.15f;
        dm.stroke(dark ? 150 : 255);
        dark = !dark;
        dm.line(x + w * f, y + w * f, x + w * (f + 0.15f), y + w * (f + 0.15f));
      }
      dm.strokeWeight(3);
      dm.stroke(150);
      dm.point(x + w * 0.2f, y + w * 0.2f);
      dm.stroke(255);
      dm.point(x + w * 0.8f, y + w * 0.8f);
    });
    stickSelector.addButton("pistonStick", "PistonStick", (x, y, w, dm) -> {
      dm.stroke(150);
      dm.strokeWeight(2);
      dm.line(x + w * 0.2f, y + w * 0.2f, x + w * 0.8f, y + w * 0.8f);
    });
    stickSelector.addButton("railStick", "RailStick", (x, y, w, dm) -> {
      dm.stroke(200);
      dm.strokeWeight(3);
      dm.line(x + w * 0.2f, y + w * 0.2f, x + w * 0.8f, y + w * 0.8f);
      dm.stroke(50);
      dm.strokeWeight(1);
      dm.line(x + w * 0.2f, y + w * 0.2f, x + w * 0.8f, y + w * 0.8f);
    });

    menu.addItem(new Button("submit", 240, 500, 120, 50, "Submit", menu, button -> {
      String shape = ((RadioSelector) button.menu.getItemById("shape")).value();
      String particleType = ((RadioSelector) button.menu.getItemById("particle")).value();
      String stickType = ((RadioSelector) button.menu.getItemById("stick")).value();
      int mass = (int) ((NumericInput) button.menu.getItemById("mass")).value;
      int countFromInput = (int) ((NumericInput) button.menu.getItemById("count")).value;
      boolean staticParticle = ((ToggleButton) button.menu.getItemById("static")).value;
      if (shape != null && particleType != null && stickType != null) {
        Particle particleTemplate;
        switch (particleType) {
        case "particle":
        default:
          particleTemplate = new Particle(0, 0, mass, sketch.particleManager);
          break;
        case "stickyParticle":
          particleTemplate = new StickyParticle(0, 0, mass, sketch.particleManager);
          break;
        case "linkableParticle":
          particleTemplate = new LinkableParticle(0, 0, mass, sketch.particleManager);
          break;
        case "plantParticle":
          particleTemplate = new PlantParticle(0, 0, mass, sketch.particleManager);
          break;
        }
        particleTemplate.isStatic = staticParticle;
        Stick stickTemplate;
        if (particleType.equals("plantParticle")) {
          stickTemplate = new PlantStick((PlantParticle) particleTemplate, (PlantParticle) particleTemplate);
        } else {
          switch (stickType) {
          case "stick":
          default:
            stickTemplate = new Stick(sketch.particleManager.defaultParticle, sketch.particleManager.defaultParticle, 0,
                1, -1);
            break;
          case "wallStick":
            stickTemplate = new WallStick(sketch.particleManager.defaultParticle,
                sketch.particleManager.defaultParticle, 0, 1, -1);
            break;
          case "pistonStick":
            stickTemplate = new PistonStick(sketch.particleManager.defaultParticle,
                sketch.particleManager.defaultParticle, 0, 1, 2, 0.1f, -1);
            break;
          case "railStick":
            stickTemplate = new RailStick(sketch.particleManager.defaultParticle,
                sketch.particleManager.defaultParticle, 0, 1, 0);
            break;
          }
        }
        switch (shape) {
        case "loop":
          sketch.mouseManager.currentInteraction = new AddConstructInteraction((scale, count) -> sketch.constructBuilder
              .addLoop(sketch.mouseX, sketch.mouseY, scale, count, particleTemplate, stickTemplate), 100,
              countFromInput, ConstructBuilder.drawLoop, sketch);
          break;
        case "triangle":
          sketch.mouseManager.currentInteraction = new AddConstructInteraction((scale, count) -> sketch.constructBuilder
              .addTriangle(sketch.mouseX, sketch.mouseY, count, scale, particleTemplate, stickTemplate), 50,
              countFromInput, ConstructBuilder.drawTriangle, sketch);
          break;
        case "rectangle":
          sketch.mouseManager.currentInteraction = new AddConstructInteraction(
              (scale, count) -> sketch.constructBuilder.addRectangle(sketch.mouseX, sketch.mouseY, count,
                  MathUtils.firstOddBefore(count), scale, true, particleTemplate, stickTemplate),
              50, countFromInput, ConstructBuilder.drawRectangle, sketch);
          break;
        case "classicRectangle":
          sketch.mouseManager.currentInteraction = new AddConstructInteraction(
              (scale, count) -> sketch.constructBuilder.addClassicRectangle(sketch.mouseX, sketch.mouseY, count, count,
                  scale, true, true, false, particleTemplate, stickTemplate),
              50, countFromInput, ConstructBuilder.drawClassicRectangle, sketch);
          break;
        case "chain":
          if (particleType.equals("plantParticle")) {
            sketch.mouseManager.currentInteraction = new PlantChainInteraction(sketch.particleManager.repelDist,
                sketch);
          } else {
            sketch.mouseManager.currentInteraction = new AddChainInteraction(sketch.particleManager.repelDist,
                particleTemplate, stickTemplate, sketch);
          }
          break;
        }
        ((RadioSelector) button.menu.getItemById("shape")).reset();
        ((RadioSelector) button.menu.getItemById("particle")).reset();
        ((RadioSelector) button.menu.getItemById("stick")).reset();
        ((NumericInput) button.menu.getItemById("mass")).reset();
        ((NumericInput) button.menu.getItemById("count")).reset();
        ((ToggleButton) button.menu.getItemById("static")).value = false;
      } else {
        button.cancelClick();
      }
    }, (x, y, w, dm) -> {
      dm.stroke(255);
      dm.strokeWeight(8);
      dm.line(x + w * 0.2f, y + w * 0.5f, x + w * 0.4f, y + w * 0.8f);
      dm.line(x + w * 0.4f, y + w * 0.8f, x + w * 0.8f, y + w * 0.2f);
    }));
  }

  private void initAddStick(Menu menu) {
    NumericInput restLengthInput = new NumericInput("restLength", 310, 50, 100, 30, "Rest Length", 100, 0, 200, menu);
    menu.addItem(restLengthInput);
    NumericInput stiffnessInput = new NumericInput("stiffness", 310, 120, 100, 30, "Stiffness", 20, 0, 20, menu) {
      @Override
      public String getValueString() {
        return Float.toString(value / 20f);
      }
    };
    menu.addItem(stiffnessInput);
    ToggleButton breakableToggle = new ToggleButton("breakable", 310, 190, 75, "Breakable", false, menu);
    menu.addItem(breakableToggle);
    NumericInput breakRatioInput = new NumericInput("breakRatio", 420, 190, 100, 30, "Break Ratio", 150, 100, 10000,
        menu) {
      @Override
      public String getValueString() {
        return Integer.toString(value) + "%";
      }
    };
    breakRatioInput.visible = false;
    menu.addItem(breakRatioInput);
    breakableToggle.setClickFunction(button -> {
      breakRatioInput.visible = button.value;
    });
    NumericInput pistonStretchFactorInput = new NumericInput("pistonStretchFactor", 310, 260, 100, 30,
        "Powered Stretch Factor", 200, 0, 1000, menu) {
      @Override
      public String getValueString() {
        return Integer.toString(value) + "%";
      }
    };
    pistonStretchFactorInput.visible = false;
    menu.addItem(pistonStretchFactorInput);
    NumericInput pistonStretchSpeedInput = new NumericInput("pistonStretchSpeed", 310, 330, 100, 30, "Stretch Speed",
        10, 1, 100, menu) {
      @Override
      public String getValueString() {
        return Float.toString(value / 100f);
      }
    };
    pistonStretchSpeedInput.visible = false;
    menu.addItem(pistonStretchSpeedInput);

    RadioSelector stickSelector = new RadioSelector("stickType", 50, 50, 250, 50, "Stick Type", menu);
    menu.addItem(stickSelector);
    stickSelector.addButton("stick", "Stick", (x, y, w, dm) -> {
      dm.stroke(255);
      dm.strokeWeight(2);
      dm.line(x + w * 0.2f, y + w * 0.2f, x + w * 0.8f, y + w * 0.8f);
    });
    stickSelector.addButton("wallStick", "WallStick", (x, y, w, dm) -> {
      dm.strokeStyle(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
      boolean dark = true;
      for (int i = 0; i < 4; i++) {
        float f = 0.2f + (float) i * 0.15f;
        dm.stroke(dark ? 150 : 255);
        dark = !dark;
        dm.line(x + w * f, y + w * f, x + w * (f + 0.15f), y + w * (f + 0.15f));
      }
      dm.strokeWeight(3);
      dm.stroke(150);
      dm.point(x + w * 0.2f, y + w * 0.2f);
      dm.stroke(255);
      dm.point(x + w * 0.8f, y + w * 0.8f);
    });
    stickSelector.addButton("pistonStick", "PistonStick", (x, y, w, dm) -> {
      dm.stroke(150);
      dm.strokeWeight(2);
      dm.line(x + w * 0.2f, y + w * 0.2f, x + w * 0.8f, y + w * 0.8f);
    });
    stickSelector.addButton("railStick", "RailStick", (x, y, w, dm) -> {
      dm.stroke(200);
      dm.strokeWeight(3);
      dm.line(x + w * 0.2f, y + w * 0.2f, x + w * 0.8f, y + w * 0.8f);
      dm.stroke(50);
      dm.strokeWeight(1);
      dm.line(x + w * 0.2f, y + w * 0.2f, x + w * 0.8f, y + w * 0.8f);
    });
    stickSelector.setClickFunction(selector -> {
      pistonStretchFactorInput.visible = selector.value().equals("pistonStick");
      pistonStretchSpeedInput.visible = selector.value().equals("pistonStick");
    });
    Button submitButton = new Button("submit", 240, 500, 120, 50, "Submit", menu, button -> {
      String stickType = stickSelector.value();
      float restLength = restLengthInput.value;
      float stiffness = stiffnessInput.value / 20f;
      float breakRatio = breakableToggle.value ? breakRatioInput.value / 100f : 0;

      if (stickType != null) {
        switch (stickType) {
        case "stick":
          sketch.mouseManager.currentInteraction = new AddStickInteraction(restLength, stiffness, sketch) {
            @Override
            public void connect(Particle next) {
              sketch.particleManager
                  .addStick(new Stick(p1, next, sketch.shiftPressed ? -1 : length, stiffness, breakRatio));
            }
          };
          break;
        case "wallStick":
          sketch.mouseManager.currentInteraction = new AddStickInteraction(restLength, stiffness, sketch) {
            @Override
            public void connect(Particle next) {
              sketch.particleManager
                  .addStick(new WallStick(p1, next, sketch.shiftPressed ? -1 : length, stiffness, breakRatio));
            }
          };
          break;
        case "pistonStick":
          float stretchFactor = pistonStretchFactorInput.value / 100f;
          float stretchSpeed = pistonStretchSpeedInput.value / 100f;
          sketch.mouseManager.currentInteraction = new AddStickInteraction(restLength, stiffness, sketch) {
            @Override
            public void connect(Particle next) {
              sketch.particleManager.addStick(new PistonStick(p1, next, sketch.shiftPressed ? -1 : length, stiffness,
                  stretchFactor, stretchSpeed, breakRatio));
            }
          };
          break;
        case "railStick":
          sketch.mouseManager.currentInteraction = new AddStickInteraction(restLength, stiffness, sketch) {
            @Override
            public void connect(Particle next) {
              sketch.particleManager
                  .addStick(new RailStick(p1, next, sketch.shiftPressed ? -1 : length, stiffness, breakRatio));
            }
          };
          break;
        }
        stickSelector.reset();
        restLengthInput.reset();
        stiffnessInput.reset();
        breakableToggle.value = false;
        pistonStretchFactorInput.reset();
        pistonStretchFactorInput.visible = false;
        pistonStretchSpeedInput.reset();
        pistonStretchSpeedInput.visible = false;
      } else {
        button.cancelClick();
      }
    }, (x, y, w, dm) -> {
      dm.stroke(255);
      dm.strokeWeight(8);
      dm.line(x + w * 0.2f, y + w * 0.5f, x + w * 0.4f, y + w * 0.8f);
      dm.line(x + w * 0.4f, y + w * 0.8f, x + w * 0.8f, y + w * 0.2f);
    });
    menu.addItem(submitButton);
  }
}
