package com.ecnu.tripmap.service;

import java.util.HashMap;

public interface SearchService {

    HashMap<String, Object> search(String query, int type, int user_id);
}
