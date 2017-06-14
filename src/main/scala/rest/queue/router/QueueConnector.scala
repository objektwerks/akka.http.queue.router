package rest.queue.router

import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client._

private[this] class Connector(val connection: Connection, val channel: Channel) {
  def isOpen: Boolean = if (connection.isOpen && channel.isOpen) true else false
  def close(): Unit = if (connection.isOpen) connection.close()
}

class QueueConsumer() extends Consumer {
  override def handleDelivery(consumerTag: String,
                              envelope: Envelope,
                              properties: BasicProperties,
                              body: Array[Byte]): Unit = {
    throw new RuntimeException("Must override handleDelivery method!")
  }

  override def handleCancel(consumerTag: String): Unit = {}

  override def handleRecoverOk(consumerTag: String): Unit = {}

  override def handleCancelOk(consumerTag: String): Unit = {}

  override def handleShutdownSignal(consumerTag: String, signal: ShutdownSignalException): Unit = {}

  override def handleConsumeOk(consumerTag: String): Unit = {}
}

class QueueConnector(conf: QueueConnectorConf) {
  private var connector = connect()

  /** A prefetchCount of 0 equals unlimited message retrieval!!! */
  def consume(prefetchCount: Int, consumer: Consumer): String = {
    checkConnector()
    connector.channel.basicQos(prefetchCount)
    connector.channel.basicConsume(conf.queueName, conf.autoAck, consumer)
  }

  def pull: Option[GetResponse] = {
    checkConnector()
    Option(connector.channel.basicGet(conf.queueName, conf.autoAck))
  }

  def push(message: String): Boolean = {
    checkConnector()
    connector.channel.basicPublish(conf.exchangeName, conf.routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes())
    connector.channel.waitForConfirms(conf.publishConfirmationTimeout)
  }

  def ack(deliveryTag: Long): Unit = {
    checkConnector()
    connector.channel.basicAck(deliveryTag, false)
  }

  def ackAllMessages(deliveryTag: Long): Unit = {
    checkConnector()
    connector.channel.basicAck(deliveryTag, true)
  }

  def nack(deliveryTag: Long): Unit = {
    checkConnector()
    connector.channel.basicNack(deliveryTag, false, true)
  }

  def close(): Unit = connector.close()

  private def connect(): Connector = {
    val connection = createConnection()
    val channel = createChannel(connection)
    new Connector(connection, channel)
  }

  private def checkConnector(): Unit = if (!connector.isOpen) connector = connect()

  private def createConnection(): Connection = {
    val factory = new ConnectionFactory()
    factory.setUri(conf.url)
    factory.newConnection()
  }

  private def createChannel(connection: Connection): Channel = {
    val channel = connection.createChannel
    channel.exchangeDeclare(conf.exchangeName, conf.exchangeType, true)
    channel.queueDeclare(conf.queueName, conf.isQueueDurable, false, false, null)
    channel.queueBind(conf.queueName, conf.exchangeName, conf.routingKey)
    channel.confirmSelect()
    channel
  }
}