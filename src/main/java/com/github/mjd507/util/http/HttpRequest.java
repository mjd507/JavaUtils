package com.github.mjd507.util.http;

import java.util.Map;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by mjd on 2018/3/24 23:11
 */
@Data
@Builder
public class HttpRequest {

  private String url;
  private String method;
  private Map<String, String> headers;
  private Map<String, Object> params;

}
