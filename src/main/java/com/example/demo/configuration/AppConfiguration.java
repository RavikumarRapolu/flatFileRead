package com.example.demo.configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import com.example.demo.model.Person;

@Configuration
@EnableBatchProcessing
public class AppConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	private Properties prop = null;
	
	private String[] colNamesArray= null;
	private Range[] colValuesRangeArray= null;

	@Bean
	public Job readCSVFilesJob() throws UnexpectedInputException, ParseException, Exception {
		return jobBuilderFactory.get("readCSVFilesJob").incrementer(new RunIdIncrementer()).start(step1()).build();
	}

	@Bean
	public Step step1() throws UnexpectedInputException, ParseException, Exception {
		return stepBuilderFactory.get("step1").<Person, Person>chunk(5).reader(reader()).writer(writer()).build();
	}

	@Bean
	public FlatFileItemReader<Person> reader() throws UnexpectedInputException, ParseException, Exception {
		loadConfig();
		FlatFileItemReaderBuilder<Person> file = new FlatFileItemReaderBuilder<Person>().name("personItemReader")
				.resource(new ClassPathResource("people.txt")).lineTokenizer(new FixedLengthTokenizer() {
					{
						System.out.println(colNamesArray);
			            setNames(colNamesArray);
						setStrict(false);
						setColumns(colValuesRangeArray);
						
					}
				});
		FlatFileItemReader<Person> last = file.fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {
			{
				setTargetType(Person.class);
			}
		}).build();

		return last;
	}

	private void loadConfig() throws IOException {
		InputStream is = null;
        try {
        	//read properties file
            this.prop = new Properties();
            is = this.getClass().getResourceAsStream("schemaConfig.properties");
            prop.load(is);
            //return unique keys in properties file...
            Set<Object> keys = this.getAllKeys();
            
            //List of columns and Range values for each column..
            List<String> colNamesList = new ArrayList<String>();
            List<Range> colValuesRangeList = new ArrayList<Range>();
            //iterate over keys from proprty files.
            for(Object k:keys){
            	String key= (String)k;
            	colNamesList.add(key);
                String[] colValues= this.getPropertyValue(key).split(",");
                int initialIndex= Integer.parseInt(colValues[0]);
                int finalIndex= Integer.parseInt(colValues[1]);
                System.out.println(key+" : "+this.getPropertyValue(key));
                colValuesRangeList.add(new Range(initialIndex,finalIndex));
            }
            //Convert list to String Array 
            colNamesArray = colNamesList.toArray(new String[colNamesList.size()]);
            colValuesRangeArray= colValuesRangeList.toArray(new Range[colValuesRangeList.size()]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}
	
	 public Set<Object> getAllKeys(){
	        Set<Object> keys = prop.keySet();
	        return keys;
	 }
	 public String getPropertyValue(String key){
	        return this.prop.getProperty(key);
	    }

	@Bean
	public ConsoleItemWriter<Person> writer() {
		return new ConsoleItemWriter<Person>();
	}
	
}
