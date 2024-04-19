package com.nursultan.order.Order;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;

@Service
public class OrderService {
    @Value("${google.maps.api.key}")
    private String api_key;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Page<Order> getOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public Order createOrder(String originLat, String originLng, String destLat, String destLng) throws IOException {
        Order order = new Order();
        order.setOriginLatitude(originLat);
        order.setOriginLongitude(originLng);
        order.setDestinationLatitude(destLat);
        order.setDestinationLongitude(destLng);
        order.setStatus("UNASSIGNED");
        String google_result = calculate(order.getOriginLatitude(),
                order.getOriginLongitude(),
                order.getDestinationLatitude(),
                order.getDestinationLongitude(),
                api_key);
        JSONObject google_json = new JSONObject(google_result);
        String status = google_json.getJSONArray("rows")
                .getJSONObject(0)
                .getJSONArray("elements")
                .getJSONObject(0)
                .getString("status");
        if (!status.equals("OK")){
            throw new IllegalArgumentException("Invalid coordinates");
        }
        int distance = google_json.getJSONArray("rows")
                .getJSONObject(0)
                .getJSONArray("elements")
                .getJSONObject(0)
                .getJSONObject("distance")
                .getInt("value");



        order.setDistance(distance);
        return orderRepository.save(order);
    }

    public boolean isValidCoordinate(String[] coordinates) {
        try {
            double latitude = Double.parseDouble(coordinates[0].trim());
            double longitude = Double.parseDouble(coordinates[1].trim());
            return !(latitude < -90) && !(latitude > 90) && !(longitude < -180) && !(longitude > 180);
        } catch (NumberFormatException e) {
            return false;
        }
    }
    @Transactional
    public boolean takeOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null && "UNASSIGNED".equals(order.getStatus())) {
            try {
                order.setStatus("TAKEN");
                orderRepository.save(order);
                return true;
            } catch (OptimisticLockingFailureException ex) {
                return false;  // Another transaction has already taken the order
            }
        }
        return false;
    }

    public String calculate(String originLat, String originLng, String destLat, String destLng, String key) throws IOException {

        OkHttpClient client = new OkHttpClient();
        String origins = originLat + "," + originLng;
        String destinations = destLat + "," + destLng;
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + origins + "&destinations=" + destinations + "&units=metric" + "&key=" + key;
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }


}
