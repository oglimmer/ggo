package de.oglimmer.ggo.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public enum Json {
	INSTANCE;

	/**
	 * Returns an object with all (but only) different attributes. If both nodes
	 * are equals returns null.
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
		StreamSupport.stream(Spliterators.spliteratorUnknownSize(newNode.fields(), Spliterator.ORDERED), false)
				.forEach(entryOfNewNode -> diffEntry(diffNode, entryOfNewNode.getKey(),
						oldNode.get(entryOfNewNode.getKey()), entryOfNewNode.getValue()));
	}

	/**
	 * @param newNode
	 * @param oldNode
	 * @param diffNode
	 */
	private void checkRemovedAttributes(JsonNode newNode, JsonNode oldNode, ObjectNode diffNode) {
		for (Iterator<Map.Entry<String, JsonNode>> it = oldNode.fields(); it.hasNext();) {
			Map.Entry<String, JsonNode> entryOfOldNode = it.next();
			String key = entryOfOldNode.getKey();
			JsonNode oldValue = entryOfOldNode.getValue();
			JsonNode newValue = newNode.get(key);
			if (oldValue != null && newValue == null) {
				diffNode.set(key, JsonNodeFactory.instance.textNode("##REMOVED##"));
			}
		}
	}

	/**
	 * Checks the with the name 'key' where oldValue is the value in the oldNode
	 * and newValue is the value in the newNode. If those value are not equals
	 * the newValue is put into diffNode under the name 'key'
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
		ArrayNode newArrayNodeRemove = JsonNodeFactory.instance.arrayNode();
		if (oldValueAsArray != null) {
			for (Iterator<JsonNode> it = oldValueAsArray.elements(); it.hasNext();) {
				JsonNode elementInOldArray = it.next();
				if (newValueAsArray == null) {
					newArrayNodeRemove.add(elementInOldArray);
				} else {
					JsonNode diffNodeForOneElement = existsOrChanged(elementInOldArray, (ArrayNode) newValueAsArray);
					if (diffNodeForOneElement != null) {
						newArrayNodeRemove.add(diffNodeForOneElement);
					}
				}
			}
		}
		if (newArrayNodeRemove.size() > 0) {
			diffNode.set(attributeName + "##REMOVED##", newArrayNodeRemove);
		}
	}

	/**
	 * @param diffNode
	 * @param attributeName
	 * @param oldValueAsArray
	 * @param newValueAsArray
	 */
	private void diffArrayAddChange(ObjectNode diffNode, String attributeName, JsonNode oldValueAsArray,
			JsonNode newValueAsArray) {
		ArrayNode newArrayNode = JsonNodeFactory.instance.arrayNode();
		for (Iterator<JsonNode> it = newValueAsArray.elements(); it.hasNext();) {
			JsonNode elementInNewArray = it.next();
			if (oldValueAsArray == null) {
				newArrayNode.add(elementInNewArray);
			} else {
				JsonNode diffNodeForOneElement = existsOrChanged(elementInNewArray, (ArrayNode) oldValueAsArray);
				if (diffNodeForOneElement != null) {
					newArrayNode.add(diffNodeForOneElement);
				}
			}
		}
		if (newArrayNode.size() > 0) {
			diffNode.set(attributeName, newArrayNode);
		}
	}

	/**
	 * Searches for nodeToSearch in arrayToSearch. Returns null if element is
	 * found and equals to nodeToSearch.
	 * 
	 * @param nodeToSearch
	 *            might be a primitive or an object
	 * @param arrayToSearch
	 *            array where elements must have the same type as nodeToSearch
	 * @return null if found, otherwise the object with all (but only) different
	 *         attributes
	 */
	private JsonNode existsOrChanged(JsonNode nodeToSearch, ArrayNode arrayToSearch) {
		if (nodeToSearch.isObject()) {
			for (Iterator<JsonNode> it = arrayToSearch.elements(); it.hasNext();) {
				JsonNode elementInArray = it.next();
				assert elementInArray.isObject();
				String nodeToSearchId = nodeToSearch.get("id").asText();
				String elementInArrayId = elementInArray.get("id").asText();
				if (nodeToSearchId.equals(elementInArrayId)) {
					return diff(nodeToSearch, elementInArray);
				}
			}
			return nodeToSearch;
		} else {
			for (Iterator<JsonNode> it = arrayToSearch.elements(); it.hasNext();) {
				JsonNode primitiveElementInArray = it.next();
				if (primitiveElementInArray.equals(nodeToSearch)) {
					return null;
				}
			}
			return nodeToSearch;
		}
	}

}
