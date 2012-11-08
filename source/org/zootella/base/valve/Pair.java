package org.zootella.base.valve;

import java.util.ArrayList;
import java.util.List;

public class Pair<Type> {
	
	// Pair
	
	/** A Pair looks at two neighboring objects in a List, a that comes right before b. */
	public Pair(Type a, Type b) {
		this.a = a;
		this.b = b;
	}

	/** The first object in this Pair. */
	public final Type a;
	/** The second object in this Pair. */
	public final Type b;
	
	// List of pairs

	/** Given a list of objects of type T, group them into pairs of T, last to first. */
	public static <Type> List<Pair<Type>> pairs(List<Type> objects) {
		List<Pair<Type>> pairs = new ArrayList<Pair<Type>>();
		if (objects.size() < 2)
			return pairs; // Not enough objects for even one Pair, return an empty list
		for (int i = objects.size() - 1; i >= 1; i--) // Start with the last Pair
			pairs.add(new Pair<Type>(objects.get(i - 1), objects.get(i))); // Add it to the list we'll return
		return pairs;
	}
}
