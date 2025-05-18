package com.example.coligo.util;

import com.example.coligo.enums.DeliveryStatus;
import com.example.coligo.model.Delivery;

@FunctionalInterface
public interface TransitionAction {
    void execute(Delivery delivery, DeliveryStatus newStatus);
}
