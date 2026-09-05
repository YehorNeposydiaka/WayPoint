package org.example.Trip.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TripStatResponse (
BigDecimal total,
BigDecimal transferTotalCost,
BigDecimal planeTransferCost,
BigDecimal carTransferCost,
BigDecimal shipTransferCost,
BigDecimal trainTransferCost,
BigDecimal busTransferCost,
BigDecimal otherTransferCost,
Long transferTime,
BigDecimal foodCost,
BigDecimal entertainmentCost,
BigDecimal landmarkCost,
BigDecimal accommodationCost,
BigDecimal shoppingCost,
BigDecimal otherCost,
BigDecimal checkpointsCost,
int transfersAmount,
int checkpointsAmount,
int preparationAmount,
BigDecimal preparationCost
){
}
