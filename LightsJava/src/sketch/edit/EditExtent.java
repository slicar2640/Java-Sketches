package sketch.edit;

import java.awt.Color;

import sketch.environment.shape.Parabola;
import sketch.util.DrawUtils;
import sketch.util.Vector;

public class EditExtent extends EditTool {
  private Parabola parabola;
  private float hoverDist = 10;

  public EditExtent(Parabola parabola) {
    this.parabola = parabola;
  }

  @Override
  public void drag(float mx, float my) {
    parabola.setExtent(distanceToVertexAlongLine(mx, my));
  }

  @Override
  public void show(DrawUtils drawUtils) {
    if (highlighted) {
      drawUtils.stroke(Color.ORANGE);
    } else {
      drawUtils.stroke(Color.WHITE);
    }
    drawUtils.strokeDash(2, 5, 5, 0);
    drawUtils.line(parabola.getP1().x, parabola.getP1().y, parabola.getP2().x, parabola.getP2().y);
  }

  @Override
  public boolean isHovered(float mx, float my) {
    return pointToSegmentDistance(parabola.getP1().x, parabola.getP1().y, parabola.getP2().x, parabola.getP2().y, mx,
        my) <= hoverDist;
  }

  private float distanceToVertexAlongLine(float mx, float my) {
    Vector vertex = parabola.getVertex();
    Vector focus = parabola.getFocus();
    Vector vertexToFocus = Vector.sub(focus, vertex);
    Vector p = new Vector(mx, my);
    float t = Vector.dot(Vector.sub(p, vertex), vertexToFocus) / vertexToFocus.magSq();
    Vector projected = Vector.add(vertex, Vector.mult(vertexToFocus, t));
    return Math.max(1, vertex.dist(projected)); // don't go behind or on top of vertex
  }

  private float pointToSegmentDistance(float x1, float y1, float x2, float y2, float px, float py) {
    Vector p1 = new Vector(x1, y1);
    Vector p2 = new Vector(x2, y2);
    Vector p = new Vector(px, py);
    float l2 = Vector.distSq(p1, p2);
    if (l2 == 0.0)
      return Vector.dist(p1, p);
    float t = Math.max(0, Math.min(1, Vector.dot(Vector.sub(p, p1), Vector.sub(p2, p1)) / l2));
    Vector projection = Vector.add(p1, Vector.sub(p2, p1).mult(t));
    return Vector.dist(p, projection);
  }
}
