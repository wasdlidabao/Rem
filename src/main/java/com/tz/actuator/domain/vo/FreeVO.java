package com.tz.actuator.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FreeVO {
    private String total;
    private String used;
    private String free;
    private String cache;
    private String available;

    @Override
    public String toString() {
        return "FreeVO{" +
                "total='" + total + '\'' +
                ", used='" + used + '\'' +
                ", free='" + free + '\'' +
                ", cache='" + cache + '\'' +
                ", available='" + available + '\'' +
                '}';
    }
}
