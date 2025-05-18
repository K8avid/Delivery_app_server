package com.example.coligo.util;

// import java.util.Objects;

// public class StatusTransition<T> {
//     private final T from;
//     private final T to;

//     public StatusTransition(T from, T to) {
//         this.from = from;
//         this.to = to;
//     }

//     public T getFrom() {
//         return from;
//     }

//     public T getTo() {
//         return to;
//     }

//     @Override
//     public boolean equals(Object obj) {
//         if (this == obj) return true;
//         if (obj == null || getClass() != obj.getClass()) return false;
//         StatusTransition<?> that = (StatusTransition<?>) obj;
//         return Objects.equals(from, that.from) && Objects.equals(to, that.to);
//     }

//     @Override
//     public int hashCode() {
//         return Objects.hash(from, to);
//     }

//     @Override
//     public String toString() {
//         return "StatusTransition{" +
//                 "from=" + from +
//                 ", to=" + to +
//                 '}';
//     }
// }




import java.util.Objects;

import com.example.coligo.enums.DeliveryStatus;

public class StatusTransition {
    private final DeliveryStatus from;
    private final DeliveryStatus to;

    public StatusTransition(DeliveryStatus from, DeliveryStatus to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        StatusTransition that = (StatusTransition) obj;
        return from == that.from && to == that.to;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
