package com.ssafy.pocketc_backend.global.dev;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DevTokenHolder {
public static final Map<Integer, String> DEV_TOKENS = Map.of(
        1, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxIiwiZW1haWwiOiJob25nQHRlc3QuY29tIn0.Jv0-x7jH18k416X4RAG_S4MoyEYjI-o8nFj8kkUf1YQ",
        2, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIyIiwiZW1haWwiOiJraW1AdGVzdC5jb20ifQ.ewg8hXC9NFnmnibpHcgazARHOtIHYgGbuqWhjS0Rxl4",
        3, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIzIiwiZW1haWwiOiJsZWVAdGVzdC5jb20ifQ.L5Ds1776lJa16fF5BO0VR-Upo4Nmi6jyXeFpQLoxQ8c",
        4, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI0IiwiZW1haWwiOiJwYXJrQHRlc3QuY29tIn0.fwJiA3ZUnW9wBKofoGHEKvhznVRcqK8VUeMDmCsd9YE",
        5, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1IiwiZW1haWwiOiJjaG9pQHRlc3QuY29tIn0.UVo_09P8EeUvfZmc4hbxL9_V5lVX-21T7fDyjwKMaow"

);
}
