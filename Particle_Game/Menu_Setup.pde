public void initStartOfAdd(Menu startOfAdd) {
  startOfAdd.addItem(new Button("p-normal", 50, 50, 120, 50, "Particle", startOfAdd, (button) -> {
    mouseManager.currentInteraction = new AddParticleInteraction(Particle.class, 100, 1);
  }
  , () -> {
    stroke(255);
    strokeWeight(0.6);
    point(0, 0);
  }
  ));
  startOfAdd.addItem(new Button("p-sticky", 50, 110, 170, 50, "StickyParticle", startOfAdd, (button) -> {
    mouseManager.currentInteraction = new AddParticleInteraction(StickyParticle.class, 100, 1);
  }
  , () -> {
    stroke(255, 255, 180);
    strokeWeight(0.6);
    point(0, 0);
  }
  ));
  startOfAdd.addItem(new Button("p-linkable", 50, 170, 190, 50, "LinkableParticle", startOfAdd, (button) -> {
    mouseManager.currentInteraction = new AddParticleInteraction(LinkableParticle.class, 100, 1);
  }
  , () -> {
    stroke(255, 0, 0);
    strokeWeight(0.6);
    point(0, 0);
  }
  ));

  startOfAdd.addItem(new LinkButton("custom", 300, 50, 120, 50, "Custom", "CustomConstruct", startOfAdd, (button) -> {
  }
  , () -> {
    stroke(255);
    strokeWeight(0.1);
    noFill();
    circle(0, 0, 0.45);
    for (int i = 0; i < 8; i++) {
      float a = (float)i / 8 * TWO_PI;
      line(0.25 * cos(a), 0.25 * sin(a), 0.35 * cos(a), 0.35 * sin(a));
    }
  }
  ));

  startOfAdd.addItem(new Button("c-loop", 300, 110, 100, 50, "Loop", startOfAdd, (button) -> {
    mouseManager.currentInteraction = new AddConstructInteraction((scale, count) -> constructBuilder.addLoop(mouseX, mouseY, scale, count), 100, 150, drawLoop);
  }
  , () -> {
    stroke(255);
    strokeWeight(0.05);
    noFill();
    circle(0, 0, 0.6);
  }
  ));
  startOfAdd.addItem(new Button("c-triangle", 300, 170, 125, 50, "Triangle", startOfAdd, (button) -> {
    mouseManager.currentInteraction = new AddConstructInteraction((scale, count) -> constructBuilder.addTriangle(mouseX, mouseY, count, scale), 50, 5, drawTriangle);
  }
  , () -> {
    stroke(255);
    strokeWeight(0.05);
    noFill();
    triangle(0, -0.3, 0.3, 0.3 * SQRT3_2, -0.3, 0.3 * SQRT3_2);
  }
  ));
  startOfAdd.addItem(new Button("c-rectangle", 300, 230, 140, 50, "Rectangle", startOfAdd, (button) -> {
    mouseManager.currentInteraction = new AddConstructInteraction((scale, count) -> constructBuilder.addRectangle(mouseX, mouseY, count, firstOddBefore(count), scale, true), 50, 5, drawRectangle);
  }
  , () -> {
    stroke(255);
    strokeWeight(0.05);
    noFill();
    rect(-0.3, -0.3, 0.6, 0.6);
  }
  ));
  startOfAdd.addItem(new Button("c-classicRectangle", 300, 290, 210, 50, "Rectangle (Classic)", startOfAdd, (button) -> {
    mouseManager.currentInteraction = new AddConstructInteraction((scale, count) -> constructBuilder.addClassicRectangle(mouseX, mouseY, count, count, scale, true, true, false), 50, 5, drawClassicRectangle);
  }
  , () -> {
    stroke(255);
    strokeWeight(0.05);
    noFill();
    rect(-0.3, -0.3, 0.6, 0.6);
    line(-0.29, -0.29, 0.29, 0.29);
  }
  ));
}

public void initCustomConstruct(Menu customConstruct) {
  RadioSelector shapeSelector = new RadioSelector("shape", 50, 50, 150, 30, "Shape", customConstruct);
  customConstruct.addItem(shapeSelector);
  shapeSelector.addButton("loop", "Loop", () -> {
    stroke(255);
    strokeWeight(0.05);
    noFill();
    circle(0, 0, 0.6);
  }
  );
  shapeSelector.addButton("triangle", "Triangle", () -> {
    stroke(255);
    strokeWeight(0.05);
    noFill();
    triangle(0, -0.3, 0.3, 0.3 * SQRT3_2, -0.3, 0.3 * SQRT3_2);
  }
  );
  shapeSelector.addButton("rectangle", "Rectangle", () -> {
    stroke(255);
    strokeWeight(0.05);
    noFill();
    rect(-0.3, -0.3, 0.6, 0.6);
  }
  );
  shapeSelector.addButton("classicRectangle", "Rectangle (Classic)", () -> {
    stroke(255);
    strokeWeight(0.05);
    noFill();
    rect(-0.3, -0.3, 0.6, 0.6);
    line(-0.29, -0.29, 0.29, 0.29);
  }
  );
  shapeSelector.addButton("chain", "Chain", () -> {
    stroke(255);
    strokeWeight(0.05);
    noFill();
    beginShape();
    vertex(-0.3, 0);
    vertex(-0.1, 0.2);
    vertex(0.1, -0.2);
    vertex(0.3, 0);
    endShape();
  }
  );

  customConstruct.addItem(new NumericInput("count", 50, 250, 100, 30, "Count", 5, 1, Integer.MAX_VALUE, customConstruct));

  RadioSelector particleSelector = new RadioSelector("particle", 225, 50, 150, 30, "Particle Type", customConstruct);
  customConstruct.addItem(particleSelector);
  particleSelector.addButton("particle", "Particle", () -> {
    stroke(255);
    strokeWeight(0.6);
    point(0, 0);
  }
  );
  particleSelector.addButton("stickyParticle", "StickyParticle", () -> {
    stroke(255, 255, 180);
    strokeWeight(0.6);
    point(0, 0);
  }
  );
  particleSelector.addButton("linkableParticle", "LinkableParticle", () -> {
    stroke(255, 0, 0);
    strokeWeight(0.6);
    point(0, 0);
  }
  );

  customConstruct.addItem(new NumericInput("mass", 250, 200, 100, 30, "Mass", 1, 1, Integer.MAX_VALUE, customConstruct));
  customConstruct.addItem(new ToggleButton("static", 270, 280, 60, "Static", false, customConstruct));

  RadioSelector stickSelector = new RadioSelector("stick", 400, 50, 150, 30, "Stick Type", customConstruct);
  customConstruct.addItem(stickSelector);
  stickSelector.addButton("stick", "Stick", () -> {
    stroke(255);
    strokeWeight(0.05);
    line(-0.3, -0.3, 0.3, 0.3);
  }
  );
  stickSelector.addButton("wallStick", "WallStick", () -> {
    strokeWeight(0.1);
    boolean dark = true;
    for (float i = -0.3; i < 0.3; i += 0.15) {
      stroke(dark ? 150 : 255);
      dark = !dark;
      line(i, i, i + 0.2, i + 0.15);
    }
  }
  );
  stickSelector.addButton("pistonStick", "PistonStick", () -> {
    stroke(150);
    strokeWeight(0.05);
    line(-0.3, -0.3, 0.3, 0.3);
  }
  );

  customConstruct.addItem(new Button("submit", 240, 500, 120, 50, "Submit", customConstruct, (button) -> {
    String shape = ((RadioSelector)button.menu.getItemById("shape")).value();
    String particleType = ((RadioSelector)button.menu.getItemById("particle")).value();
    String stickType = ((RadioSelector)button.menu.getItemById("stick")).value();
    int mass = ((NumericInput)button.menu.getItemById("mass")).value;
    int countFromInput = ((NumericInput)button.menu.getItemById("count")).value;
    boolean staticParticle = ((ToggleButton)button.menu.getItemById("static")).value;
    if (shape != null && particleType != null && stickType != null) {
      Particle particleTemplate;
      switch(particleType) {
      case "particle":
      default:
        particleTemplate = new Particle(0, 0, mass, 0, particleManager);
        break;
      case "stickyParticle":
        particleTemplate = new StickyParticle(0, 0, mass, 0, particleManager);
        break;
      case "linkableParticle":
        particleTemplate = new LinkableParticle(0, 0, mass, 0, particleManager);
        break;
      }
      particleTemplate.isStatic = staticParticle;
      Stick stickTemplate;
      switch(stickType) {
      case "stick":
      default:
        stickTemplate = new Stick(constructBuilder.defaultParticle, constructBuilder.defaultParticle, 0, 1, -1);
        break;
      case "wallStick":
        stickTemplate = new WallStick(constructBuilder.defaultParticle, constructBuilder.defaultParticle, 0, 1, -1);
        break;
      case "pistonStick":
        stickTemplate = new PistonStick(constructBuilder.defaultParticle, constructBuilder.defaultParticle, 0, 1, 2, 0.1, -1);
        break;
      }
      switch(shape) {
      case "loop":
        mouseManager.currentInteraction = new AddConstructInteraction((scale, count) -> constructBuilder.addLoop(mouseX, mouseY, scale, count, particleTemplate, stickTemplate), 100, countFromInput, drawLoop);
        break;
      case "triangle":
        mouseManager.currentInteraction = new AddConstructInteraction((scale, count) -> constructBuilder.addTriangle(mouseX, mouseY, count, scale, particleTemplate, stickTemplate), 50, countFromInput, drawTriangle);
        break;
      case "rectangle":
        mouseManager.currentInteraction = new AddConstructInteraction((scale, count) -> constructBuilder.addRectangle(mouseX, mouseY, count, firstOddBefore(count), scale, true, particleTemplate, stickTemplate), 50, countFromInput, drawRectangle);
        break;
      case "classicRectangle":
        mouseManager.currentInteraction = new AddConstructInteraction((scale, count) -> constructBuilder.addClassicRectangle(mouseX, mouseY, count, count, scale, true, true, false, particleTemplate, stickTemplate), 50, countFromInput, drawClassicRectangle);
        break;
      case "chain":
        mouseManager.currentInteraction = new AddChainInteraction(particleManager.bucketSize, particleTemplate, stickTemplate);
        break;
      }
      ((RadioSelector)button.menu.getItemById("shape")).reset();
      ((RadioSelector)button.menu.getItemById("particle")).reset();
      ((RadioSelector)button.menu.getItemById("stick")).reset();
      ((NumericInput)button.menu.getItemById("mass")).reset();
      ((NumericInput)button.menu.getItemById("count")).reset();
    } else {
      ((Button)button).cancelClick();
    }
  }
  , () -> {
    stroke(255);
    strokeWeight(0.1);
    line(-0.3, 0, -0.1, 0.3);
    line(-0.1, 0.3, 0.3, -0.3);
  }
  ));
}
