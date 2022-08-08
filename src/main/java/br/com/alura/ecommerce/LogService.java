package br.com.alura.ecommerce;

import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class LogService {

    public static void main(String[] args) {
    	LogService logService = new LogService();
    	
    	try(var service = new KafkaService(LogService.class.getSimpleName(), Pattern.compile("ECOMMERCE.*"), logService::parse)) { //parse eh a funcao que vou executar para cada mensagem que recebo
    		service.run();
    	}
    }
    
    private void parse(ConsumerRecord<String, String> record) {
    	System.out.println("------------------------------------------");
        System.out.println("LOG: " + record.topic());
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());
    }

}
