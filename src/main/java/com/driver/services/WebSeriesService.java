package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //In case the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Don't forget to save the production and webseries Repo

        // Checking Whether the WebSeries with the Same Name Exist or Not
        if(webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName())!=null){
            throw new Exception("Series is already present");
        }

        // Creating a WebSeries Object and Setting it's all attributes
        WebSeries webSeries=new WebSeries(webSeriesEntryDto.getSeriesName(), webSeriesEntryDto.getAgeLimit(), webSeriesEntryDto.getRating(), webSeriesEntryDto.getSubscriptionType());

        // Getting the ProductionHouse and updating it's WebSeries List
        ProductionHouse productionHouse=productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId()).get();
        List<WebSeries> webSeriesList=productionHouse.getWebSeriesList();
        webSeriesList.add(webSeries);
        int size=webSeriesList.size();
        double avgRatings=0;

        for(WebSeries series:webSeriesList){
            avgRatings+=series.getRating();
        }

        // Updating Rating & WebSeriesList attributes of ProductionHouse
        productionHouse.setRatings(avgRatings/size);
        productionHouse.setWebSeriesList(webSeriesList);

        // Setting the ProductionHouse attributes of WebSeries
        webSeries.setProductionHouse(productionHouse);

        // Saving the ProductionHouse Object & since it is Parent of WebSeries and having CASCADE All, so it also save the WebSeries Object
        productionHouseRepository.save(productionHouse);

        WebSeries newWebSeries=webSeriesRepository.save(webSeries);

        //return null;
        return newWebSeries.getId();
    }

}
