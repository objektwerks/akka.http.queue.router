package rest.queue.router

import com.rabbitmq.client._

private[this] class Connector(val connection: Connection, val channel: Channel) {
  def isOpen: Boolean = if (connection.isOpen && channel.isOpen) true else false
  def close(): Unit = if (connection.isOpen) connection.close()
}

class QueueConnector(conf: QueueConnectorConf) {
  private var connector = connect()

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