package prog07;
import java.util.*;
import java.awt.Graphics;

public class SkipList <K extends Comparable<K>, V>
  extends AbstractMap<K, V> {

  private static class Node <K extends Comparable<K>, V>
    implements Map.Entry<K, V> {

    K key;
    Object valueOrDown;
    Node<K, V> next;
    
    Node () {}

    Node (K key, V value, Node<K, V> next) {
      this.key = key;
      this.valueOrDown = value;
      this.next = next;
    }
    
    Node (K key, Node<K, V> down, Node<K, V> next) {
      this.key = key;
      this.valueOrDown = down;
      this.next = next;
    }
    
    boolean bottom () {
      return valueOrDown == null || !(valueOrDown instanceof Node);
    }

    public K getKey () { return key; }
    public V getValue () { return (V) valueOrDown; }
    public V setValue (V newValue) {
      V oldValue = getValue();
      valueOrDown = newValue;
      return oldValue;
    }

    Node<K, V> getDown () { return (Node<K,V>) valueOrDown; }
  }
  
  private Node<K, V> dummy = new Node<K,V>();
  private int size;
  
  boolean heads () { return Math.random() < 0.5; }

  /**
   * Find the node with the given key.
   * @param key The key to be found.
   * @param from The node from which to start the search.
   * @return The node with that key.
   */
  private Node<K, V> find (K key, Node<K, V> from) {
    while (from.next != null && key.compareTo(from.next.key) > 0)
      from = from.next;

    if (from.bottom()) {
      if (from.next != null && key.equals(from.next.key))
        return from.next;
      else
        return null;
    }
    else
      return find(key, from.getDown());
  }
  
  /**
   * Add a new key value to the sorted list, using from as the starting point.
   * @param key The new key.
   * @param value The new value.
   * @param from The node from which to start the search.
   * @return The new node.
   */
  private Node<K, V> add (K key, V value, Node<K, V> from) {
    // Move forward to the node just before where value belongs.
	  if(from.bottom()){
		  while (from.next != null && key.compareTo(from.next.key) > 0){
			  from= from.next;
		  }
		Node<K,V> node= new Node(key,value, from.next);
		from.next= node;
		size++;
		return node;
	  } else {
		  while(from.next != null && key.compareTo(from.next.key) > 0){
			  from= from.next;
		  }
		  Node<K,V> nextLevel = add(key,value,from.getDown());
		  if(heads() && nextLevel != null){
			  Node<K,V> nextNode= new Node(key, nextLevel, from.next);
			  from.next= nextNode;
			  return nextNode;
		  }
	  }
	  return null;
  }
	  
	  
  
    
  /**
   * Remove the node with a given key, if it is there, starting from from.
   * @param key The key to be removed.
   * @param from The node from which to start the search.
   * @return The value being removed, if it is there, or null.
   */
  private V remove (K key, Node<K, V> from) {
    // Move forward to the node just before value or just before where
    // value belongs.
	 Node<K,V>found= find(key,from);
	if( from.bottom() && found != null){
		 V value= found.getValue();
		 while(from.next != null && from.next.key.compareTo(found.key) > 0){
			 from= from.next;
		 }
	 

	 if(from.next != null){
		 from.next= from.next.next;
	 }
	 
	 size--;
	 return value;
  } else if (found != null){
	  while(from.next != null && from.next.key.compareTo(found.key) < 0){
		  from= from.next;
	  }
	  if(from.next != null){
		  from.next = from.next.next;
	  }
	  remove(key, from.getDown());
	  
  }
	return null;
  }
    // If the key is in this list, remove it.

    // If we are on the bottom, return the value or null.

    // Otherwise, recurse.
  

  public V remove (Object keyAsObject) {
    K key = (K) keyAsObject;
    V value = remove(key, dummy);
    while (!dummy.bottom() && dummy.next == null)
      dummy = dummy.getDown();
    return value;
  }

  public boolean containsKey (Object key) {
    return find((K) key, dummy) != null;
  }
  
  public V get (Object key) {
    Node<K, V> node = find((K) key, dummy);
    if (node != null)
      return node.getValue();
    return null;
  }
  
  public boolean isEmpty () { return size == 0; }
  
  /**
   * See Map interface for details.
   */
  public V put (K key, V value) {
    if(find(key,dummy) != null){
    	find(key,dummy).setValue(value);
    	return value;
    }
    Node<K,V> newNode= add(key,value,dummy);
    while(newNode != null && heads()){
    	//draw();
    	Node topNode= new Node(key, newNode, null);
    	dummy= new Node(null, dummy, topNode);
    	newNode= topNode;
    }
    return value;
  }      
  
  private static class Iter<K extends Comparable<K>, V> implements Iterator<Map.Entry<K, V>> {
    Node<K, V> node;
    
    Iter (Node<K, V> dummy) {
      for (node = dummy; !node.bottom(); node = node.getDown());
    }
    
    public boolean hasNext () {
      return node.next != null;
    }
    
    public Map.Entry<K, V> next () {
      node = node.next;
      return node;
    }
    
    public void remove () {
      throw new UnsupportedOperationException();
    }
  }
  
  private class Setter extends AbstractSet<Map.Entry<K, V>> {
    public Iterator<Map.Entry<K, V>> iterator () {
      return new Iter(dummy);
    }
    
    public int size () { return size; }
  }
  
  public Set<Map.Entry<K, V>> entrySet () { return new Setter(); }
  
  public void draw() {
    
    Node<K, V> node;
    int depth;
    for (depth = 1, node = dummy; !node.bottom(); node = node.getDown(), depth++);
    
    DrawingPanel dp = new DrawingPanel(size * 30 + 40, depth * 30 + 40);
    
    Map<K, Integer> map = new HashMap<K, Integer>();
    
    draw(dummy, dp, map);
    
  }
  
  private int draw(Node<K, V> node, DrawingPanel dp, Map<K, Integer> map) {
    if (node.bottom()) {
      int i = 10;
      Graphics g = dp.getGraphics();
      
      for (; node != null; node = node.next, i += 30) {
        g.drawRect(i, dp.getHeight() - 50, 20, 20);
        
        if (node.getKey() != null) {
					g.drawString(node.getValue().toString(), i + 10, dp.getHeight() - 5);
					g.drawLine(i + 10, dp.getHeight() - 30, i + 10, dp.getHeight() - 20);
					map.put(node.key, i);
        }
        
        if (node.next != null) {
          g.drawLine(i + 20, dp.getHeight() - 40, i + 30, dp.getHeight() - 40);
        }
        
      }
      return dp.getHeight() - 80;
    } else {
      int height = draw(node.getDown(), dp, map);
      int i = 10;
      Graphics g = dp.getGraphics();
      
      for (; node != null; node = node.next, i = node != null ? (map.get(node.key)) : -1) {
        g.drawRect(i, height, 20, 20);
	
        if (node.next != null)
          g.drawLine(i + 20, height + 10, map.get(node.next.key), height + 10);
        
        g.drawLine(i + 10, height + 20, i + 10, height + 30);
      }
      return height - 30;
    }
  }

  public static void main (String[] args) {
    Map<String, Integer> map = new SkipList<String, Integer>();
    
    map.put("Victor", 50);
    map.put("Irina", 45);
    map.put("Lisa", 47);
    map.put("Bobby", 66);
    map.put("Matt", 12);
    
    for (Map.Entry<String, Integer> pair : map.entrySet())
      System.out.println(pair.getKey() + " " + pair.getValue());
    
    System.out.println(map.get("Irina"));
    map.remove("Irina");
    
    for (Map.Entry<String, Integer> pair : map.entrySet())
      System.out.println(pair.getKey() + " " + pair.getValue());
    
    System.out.println(map.get("Irina"));
  }
}
