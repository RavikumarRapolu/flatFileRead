package com.example.demo.configuration;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
 
public class ConsoleItemWriter<T> implements ItemWriter<T> { 
    @Override
    public void write(List<? extends T> items) throws Exception {
    	System.out.println("<><><><<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>");
        for (T item : items) { 
            System.out.println(item); 
        } 
    } 
}
