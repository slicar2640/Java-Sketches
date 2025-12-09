package sketch.menu.item;

@FunctionalInterface
public interface ClickFunction<T extends MenuItem> {
  void run(T item);
}
