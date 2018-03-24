import datetime, time

from kafka import KafkaProducer
from flask import current_app

kafkaProducer = None

class MessagingService:
    
    def producer(self) -> KafkaProducer:
        global kafkaProducer
        if kafkaProducer is None:
            kafkaProducer = KafkaProducer(bootstrap_servers=current_app.config['MESSAGE_BROKER'] + ':9092')
        return kafkaProducer

    def logRecord(self, entity: str, operation: str, duration: int) -> str:
        now = datetime.datetime.now()
        return now.strftime('%Y|%m|%d|%H|%M') + '|' + entity + '|' + operation + '|' + str(duration)

    def log(self, entity: str, operation: str, duration: int):
        if current_app.config['MESSAGING_ENABLED'] == 'true':
            self.producer().send(current_app.config['MESSAGE_TOPIC'], self.logRecord(entity, operation, duration).encode("utf-8"))

