package de.oglimmer.ggo.websocket.util;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public enum Json {
	INSTANCE;

	/**
	 * Returns an object with all (but only) different attributes. If both nodes are equals returns null.
	 * 
	 * @param newNode
	 * @param oldNode
	 * @return
	 */
	public JsonNode diff(JsonNode newNode, JsonNode oldNode) {
		ObjectNode diffNode = JsonNodeFactory.instance.objectNode();
		checkAddedChangedAttributes(newNode, oldNode, diffNode);
		checkRemovedAttributes(newNode, oldNode, diffNode);
		return diffNode.size() > 0 ? diffNode : null;

	}

	/**
	 * @param newNode
	 * @param oldNode
	 * @param diffNode
	 */
	private void checkAddedChangedAttributes(JsonNode newNode, JsonNode oldNode, ObjectNode diffNode) {
		entrySetStream(newNode).forEach(entryOfNewNode -> diffEntry(diffNode, entryOfNewNode.getKey(),
				oldNode.get(entryOfNewNode.getKey()), entryOfNewNode.getValue()));
	}

	/**
	 * @param newNode
	 * @param oldNode
	 * @param diffNode
	 */
	private void checkRemovedAttributes(JsonNode newNode, JsonNode oldNode, ObjectNode diffNode) {
		entrySetStream(oldNode).filter(
				entryOfOldNode -> entryOfOldNode.getValue() != null && newNode.get(entryOfOldNode.getKey()) == null)
				.forEach(entryOfOldNode -> diffNode.set(entryOfOldNode.getKey(),
						JsonNodeFactory.instance.textNode("##REMOVED##")));
	}

	/**
	 * Checks the with the name 'key' where oldValue is the value in the oldNode and newValue is the value in the
	 * newNode. If those value are not equals the newValue is put into diffNode under the name 'key'
	 * 
	 * @param diffNode
	 * @param key
	 * @param oldValue
	 * @param newValue
	 */
	private void diffEntry(ObjectNode diffNode, String key, JsonNode oldValue, JsonNode newValue) {
		if (newValue != null && newValue.isArray()) {
			diffArray(diffNode, key, oldValue, newValue);
		} else if (newValue != null && newValue.isObject()) {
			diffObject(diffNode, key, oldValue, newValue);
		} else if (newValue != null && newValue.isNull() && oldValue != null && !oldValue.isNull()) {
			// special case: set object to null
			diffNode.set(key, JsonNodeFactory.instance.nullNode());
		} else {
			diffPrimitive(diffNode, key, oldValue, newValue);
		}
	}

	/**
	 * 
	 * 
	 * @param diffNode
	 * @param key
	 * @param oldValue
	 * @param newValue
	 */
	private void diffPrimitive(ObjectNode diffNode, String key, JsonNode oldValue, JsonNode newValue) {
		assert newValue == null || !newValue.isContainerNode();
		assert oldValue == null || !oldValue.isContainerNode();
		if ((newValue == null && oldValue != null) || (newValue != null && !newValue.equals(oldValue))) {
			diffNode.set(key, newValue);
		}
	}

	/**
	 * @param diffNode
	 * @param key
	 * @param oldValue
	 * @param newValue
	 */
	private void diffObject(ObjectNode diffNode, String key, JsonNode oldValue, JsonNode newValue) {
		JsonNode nextedDiff = oldValue != null ? diff(newValue, oldValue) : newValue;
		if (nextedDiff != null) {
			diffNode.set(key, nextedDiff);
		}
	}

	/**
	 * @param diffNode
	 * @param attributeName
	 *            someIntArray
	 * @param oldValueAsArray
	 *            [1,2,3,4,5]
	 * @param newValueAsArray
	 *            [1,2,3,4]
	 */
	private void diffArray(ObjectNode diffNode, String attributeName, JsonNode oldValueAsArray,
			JsonNode newValueAsArray) {
		assert newValueAsArray.isArray();
		assert oldValueAsArray == null || oldValueAsArray.isArray();
		diffArrayAddChange(diffNode, attributeName, oldValueAsArray, newValueAsArray);
		diffArrayRemove(diffNode, attributeName, oldValueAsArray, newValueAsArray);
	}

	/**
	 * @param diffNode
	 * @param attributeName
	 * @param oldValueAsArray
	 * @param newValueAsArray
	 */
	private void diffArrayRemove(ObjectNode diffNode, String attributeName, JsonNode oldValueAsArray,
			JsonNode newValueAsArray) {
		diffArrayAgainstArray(diffNode, attributeName + "##REMOVED##", newValueAsArray, oldValueAsArray);
	}

	/**
	 * @param diffNode
	 * @param attributeName
	 * @param oldValueAsArray
	 * @param newValueAsArray
	 */
	private void diffArrayAddChange(ObjectNode diffNode, String attributeName, JsonNode oldValueAsArray,
			JsonNode newValueAsArray) {
		diffArrayAgainstArray(diffNode, attributeName, oldValueAsArray, newValueAsArray);
	}

	/**
	 * @param diffNode
	 * @param attributeName
	 * @param previousArray
	 * @param latestArray
	 */
	private void diffArrayAgainstArray(ObjectNode diffNode, String attributeName, JsonNode previousArray,
			JsonNode latestArray) {
		ArrayNode newArrayNode = JsonNodeFactory.instance.arrayNode();
		if (latestArray != null) {
			elementsStream(latestArray)
					.forEach(elementInNewArray -> diffArraySearch(newArrayNode, previousArray, elementInNewArray));
		}
		if (newArrayNode.size() > 0) {
			diffNode.set(attributeName, newArrayNode);
		}
	}

	/**
	 * @param arrayToSearchThrough
	 * @param targetArray
	 * @param nodeToFind
	 */
	private void diffArraySearch(ArrayNode targetArray, JsonNode arrayToSearchThrough, JsonNode nodeToFind) {
		if (arrayToSearchThrough == null) {
			targetArray.add(nodeToFind);
		} else {
			JsonNode diffNodeForOneElement = existsOrChanged(nodeToFind, (ArrayNode) arrayToSearchThrough);
			if (diffNodeForOneElement != null) {
				targetArray.add(diffNodeForOneElement);
			}
		}
	}

	/**
	 * Searches for nodeToSearch in arrayToSearch. Returns null if element is found and equals to nodeToSearch.
	 * 
	 * @param nodeToFind
	 *            might be a primitive or an object
	 * @param arrayToSearchThrough
	 *            array where elements must have the same type as nodeToSearch
	 * @return null if found, otherwise the object with all (but only) different attributes
	 */
	private JsonNode existsOrChanged(JsonNode nodeToFind, ArrayNode arrayToSearchThrough) {
		if (nodeToFind.isObject()) {
			for (Iterator<JsonNode> it = arrayToSearchThrough.elements(); it.hasNext();) {
				JsonNode elementInArray = it.next();
				assert elementInArray.isObject();
				String nodeToSearchId = nodeToFind.get("id").asText();
				String elementInArrayId = elementInArray.get("id").asText();
				if (nodeToSearchId.equals(elementInArrayId)) {
					return diff(nodeToFind, elementInArray);
				}
			}
			return nodeToFind;
		} else {
			return elementsStream(arrayToSearchThrough).anyMatch(e -> e.equals(nodeToFind)) ? null : nodeToFind;
		}
	}

	private Stream<Entry<String, JsonNode>> entrySetStream(JsonNode newNode) {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(newNode.fields(), Spliterator.ORDERED), false);
	}

	private Stream<JsonNode> elementsStream(JsonNode newNode) {
		return StreamSupport.stream(newNode.spliterator(), false);
	}

}
