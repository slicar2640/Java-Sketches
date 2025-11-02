PushInteraction pushInteraction = new PushInteraction(5, 100);
DeleteInteraction deleteInteraction = new DeleteInteraction(50);
AddStickInteraction addStickInteraction = new AddStickInteraction(100, 1);

BiConsumer<Float, Integer> drawLoop = (scale, count) -> {
  noFill();
  stroke(255);
  strokeWeight(max(0.9 + sin((float)frameCount / 20), 0));
  circle(mouseX, mouseY, scale * 2);
};

BiConsumer<Float, Integer> drawTriangle = (scale, count) -> {
  noFill();
  stroke(255);
  strokeWeight(max(0.9 + sin((float)frameCount / 20), 0));
  float sideLength = scale * (count - 1);
  beginShape();
  vertex(mouseX, mouseY - sideLength * SQRT3_2 * 2 / 3);
  vertex(mouseX + sideLength / 2, mouseY + sideLength * SQRT3_2 / 3);
  vertex(mouseX - sideLength / 2, mouseY + sideLength * SQRT3_2 / 3);
  endShape(CLOSE);
};

BiConsumer<Float, Integer> drawRectangle = (scale, count) -> {
  noFill();
  stroke(255);
  strokeWeight(max(0.9 + sin((float)frameCount / 20), 0));
  rect(mouseX - (count - 1) * scale / 2, mouseY - (count - 1) * scale * SQRT3_2 / 2, (count - 1) * scale, (count - 1) * scale * SQRT3_2);
};

BiConsumer<Float, Integer> drawClassicRectangle = (scale, count) -> {
  noFill();
  stroke(255);
  strokeWeight(max(0.9 + sin((float)frameCount / 20), 0));
  rect(mouseX - (count - 1) * scale / 2, mouseY - (count - 1) * scale / 2, (count - 1) * scale, (count - 1) * scale);
};
