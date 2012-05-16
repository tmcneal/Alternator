package com.michelboudreau.test;

import com.amazonaws.services.dynamodb.model.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext.xml"})
public class AlternatorItemTest extends AlternatorTest {

	String testTableName;

	@Before
	public void setUp() throws Exception {
		testTableName = "Testing";
		client.createTable(new CreateTableRequest().withTableName("Testing").withKeySchema(new KeySchema().withHashKeyElement(getSSchema()).withRangeKeyElement(getSSchema())));
	}

	@After
	public void tearDown() throws Exception {
		DeleteTableRequest del = new DeleteTableRequest();
		del.setTableName("Testing");
		client.deleteTable(del);
	}

	@Test
	public void putItemInTableTest() {
		PutItemRequest request = new PutItemRequest();
		request.setTableName(testTableName);
		request.setItem(generateStaticItem());
		PutItemResult res = client.putItem(request);
		Assert.assertEquals(res.getAttributes(), generateStaticItem());
		getItemTest();
	}

	@Test
	public void updateItemInTableTest() {
		UpdateItemResult res = client.updateItem(getUpdateItemRequest());
		Assert.assertEquals(res.getAttributes(), generateStaticItem());
		getNewItemTest();
	}

	@Test
	public void deleteItemInTableTest() {
		DeleteItemRequest request = new DeleteItemRequest();
		request.setKey(getHashKey());
		DeleteItemResult result = client.deleteItem(request);
		getDeletedItemTest();
	}

	@Test
	public void batchWriteItemInTableTest() {
		BatchWriteItemResult result = client.batchWriteItem(generateWriteBatchRequest());
		Assert.assertNotNull(result);
	}

	@Test
	public void batchGetItemInTableTest() {
		BatchGetItemResult result = client.batchGetItem(generateGetBatchRequest());
		Assert.assertNotNull(result);
	}

	@Test
	public void batchGetItemInTableWithoutKeyTest() {
		BatchGetItemRequest batchGetItemRequest = new BatchGetItemRequest();
		Map<String, KeysAndAttributes> requestItems = new HashMap<String, KeysAndAttributes>();
		requestItems.put(testTableName, null);
		batchGetItemRequest.withRequestItems(requestItems);
		Assert.assertNull(client.batchGetItem(batchGetItemRequest).getResponses());
	}

	@Test
	public void batchGetItemInTableWithoutNameTest() {
		BatchGetItemRequest batchGetItemRequest = new BatchGetItemRequest();
		Map<String, KeysAndAttributes> requestItems = new HashMap<String, KeysAndAttributes>();
		Key table1key1 = new Key().withHashKeyElement(new AttributeValue().withS("123"));
		requestItems.put(null, new KeysAndAttributes().withKeys(table1key1));
		batchGetItemRequest.withRequestItems(requestItems);
		Assert.assertNull(client.batchGetItem(batchGetItemRequest).getResponses());
	}

	@Test
	public void batchGetItemInTableWithoutRequestItemsTest() {
		Assert.assertNull(client.batchGetItem(new BatchGetItemRequest()).getResponses());
	}

	@Test
	public void deleteItemWithoutTableNameTest() {
		DeleteItemRequest delete = new DeleteItemRequest();
		delete.setKey(getHashKey());
		Assert.assertNull(client.deleteItem(delete).getAttributes());
	}

	@Test
	public void deleteItemWithoutKeyTest() {
		DeleteItemRequest delete = new DeleteItemRequest();
		delete.setTableName(testTableName);
		Assert.assertNull(client.deleteItem(delete).getAttributes());
	}

	@Test
	public void getItemWithoutTableNameTest() {
		GetItemRequest request = new GetItemRequest();
		request.setKey(new Key().withHashKeyElement(new AttributeValue().withNS("123")));
		GetItemResult res = client.getItem(request);
		Assert.assertNull(res.getItem());
	}

	@Test
	public void getItemWithoutKeyTest() {
		GetItemRequest request = new GetItemRequest();
		request.setTableName(testTableName);
		GetItemResult res = client.getItem(request);
		Assert.assertNull(res.getItem());
	}

	@Test
	public void putItemWithoutTableNameTest() {
		PutItemRequest req = new PutItemRequest();
		req.setItem(generateStaticItem());
		Assert.assertNull(client.putItem(req).getAttributes());
	}

	@Test
	public void putItemWithoutitemTest() {
		PutItemRequest req = new PutItemRequest();
		req.setTableName(testTableName);
		Assert.assertNull(client.putItem(req).getAttributes());
	}

	@Test
	public void updateItemWithoutTableNameTest() {
		UpdateItemRequest req = new UpdateItemRequest();
		req.setKey(getHashKey());
		Assert.assertNull(client.updateItem(req).getAttributes());
	}

	@Test
	public void updateItemWithoutKeyTest() {
		UpdateItemRequest req = new UpdateItemRequest();
		req.setTableName(testTableName);
		Assert.assertNull(client.updateItem(req).getAttributes());
	}


	public BatchGetItemRequest generateGetBatchRequest() {
		BatchGetItemRequest batchGetItemRequest = new BatchGetItemRequest();
		Map<String, KeysAndAttributes> requestItems = new HashMap<String, KeysAndAttributes>();
		Key table1key1 = new Key().withHashKeyElement(new AttributeValue().withS("123"));
		requestItems.put(testTableName, new KeysAndAttributes().withKeys(table1key1));
		batchGetItemRequest.withRequestItems(requestItems);
		return batchGetItemRequest;
	}

	public BatchWriteItemRequest generateWriteBatchRequest() {
		BatchWriteItemRequest request = new BatchWriteItemRequest();
		Map<String, List<WriteRequest>> requestItems = new HashMap<String, List<WriteRequest>>();
		List<WriteRequest> itemList = new ArrayList<WriteRequest>();
		itemList.add(new WriteRequest().withPutRequest(new PutRequest().withItem(generateStaticItem())));
		itemList.add(new WriteRequest().withPutRequest(new PutRequest().withItem(generateNewStaticItem())));
		requestItems.put(testTableName, itemList);
		request.setRequestItems(requestItems);
		return request;
	}

	public Map<String, AttributeValue> generateStaticItem() {
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		item.put("id", new AttributeValue().withS("123"));
		item.put("date", new AttributeValue().withN("56789"));
		item.put("testfield", new AttributeValue().withS("test"));
		return item;
	}

	public Map<String, AttributeValue> generateNewStaticItem() {
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		item.put("id", new AttributeValue().withS("123"));
		item.put("date", new AttributeValue().withN("56789"));
		item.put("testfield", new AttributeValue().withS("newtest"));
		return item;
	}

	public Key getHashKey() {
		return new Key().withHashKeyElement(new AttributeValue().withS("123"));
	}

	public UpdateItemRequest getUpdateItemRequest() {
		UpdateItemRequest request = new UpdateItemRequest();
		request.setTableName(testTableName);
		Map<String, AttributeValueUpdate> item = new HashMap<String, AttributeValueUpdate>();
		item.put("testfield", new AttributeValueUpdate().withValue(new AttributeValue().withS("newtest")));
		request.setAttributeUpdates(item);
		request.setKey(new Key().withHashKeyElement(new AttributeValue().withNS("123")));
		return request;
	}


	public KeySchemaElement getSSchema() {
		KeySchemaElement el = new KeySchemaElement();
		el.setAttributeName("id");
		el.setAttributeType(ScalarAttributeType.S);
		return el;
	}

	public KeySchemaElement getNSchema() {
		KeySchemaElement el = new KeySchemaElement();
		el.setAttributeName("date");
		el.setAttributeType(ScalarAttributeType.N);
		return el;
	}

	@Ignore
	@Test
	public void getItemTest() {
		GetItemRequest request = new GetItemRequest();
		request.setKey(new Key().withHashKeyElement(new AttributeValue().withNS("123")));
		request.setTableName(testTableName);
		GetItemResult res = client.getItem(request);
		Assert.assertEquals(res.getItem(), generateStaticItem());
	}

	@Ignore
	@Test
	public void getNewItemTest() {
		GetItemRequest request = new GetItemRequest();
		request.setKey(new Key().withHashKeyElement(new AttributeValue().withNS("123")));
		request.setTableName(testTableName);
		GetItemResult res = client.getItem(request);
		Assert.assertEquals(res.getItem(), generateNewStaticItem());
	}

	@Ignore
	@Test
	public void getDeletedItemTest() {
		GetItemRequest request = new GetItemRequest();
		request.setKey(new Key().withHashKeyElement(new AttributeValue().withNS("123")));
		GetItemResult res = client.getItem(request);
		Assert.assertEquals(res.getItem(), null);
	}


}
