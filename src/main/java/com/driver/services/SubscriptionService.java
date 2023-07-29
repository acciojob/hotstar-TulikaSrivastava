package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay

        Date date=new Date();
        int total=calculateTotalAmount(subscriptionEntryDto);

        // Creating a Subscription Object and setting it's all attributes
        Subscription subscription=new Subscription(subscriptionEntryDto.getSubscriptionType(), subscriptionEntryDto.getNoOfScreensRequired(), date, total);

        User user=userRepository.findById(subscriptionEntryDto.getUserId()).get();
        user.setSubscription(subscription);

        subscription.setUser(user);


        userRepository.save(user);

        return total;
    }
    private int calculateTotalAmount(SubscriptionEntryDto subscriptionEntryDto){
        switch (subscriptionEntryDto.getSubscriptionType().toString()){
            case "BASIC":
                return 500+(200*subscriptionEntryDto.getNoOfScreensRequired());
            case "PRO":
                return 800+(250*subscriptionEntryDto.getNoOfScreensRequired());
            case "ELITE":
                return 1000+(350*subscriptionEntryDto.getNoOfScreensRequired());
        }
        return -1;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository


        User user=userRepository.findById(userId).get();

        int prevPrice=user.getSubscription().getTotalAmountPaid();
        int currPrice=0;

        if(user.getSubscription().getSubscriptionType()==SubscriptionType.ELITE){
            throw new Exception("Already the best Subscription");
        }
        else if(user.getSubscription().getSubscriptionType()==SubscriptionType.BASIC){
            currPrice=800+(250* user.getSubscription().getNoOfScreensSubscribed());
            user.getSubscription().setSubscriptionType(SubscriptionType.PRO);
            // user.getSubscription().setNoOfScreensSubscribed(250);  // No need
        }
        else  {
            currPrice=1000+(350* user.getSubscription().getNoOfScreensSubscribed());
            user.getSubscription().setSubscriptionType(SubscriptionType.ELITE);
            //user.getSubscription().setNoOfScreensSubscribed(350);
        }

        userRepository.save(user);

        //return null;
        return currPrice-prevPrice;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        int revenue=0;

        List<Subscription> subscriptions=subscriptionRepository.findAll();

        for (Subscription subscription:subscriptions){
            revenue+=subscription.getTotalAmountPaid();
        }
        //return null;

        return revenue;
    }

}
