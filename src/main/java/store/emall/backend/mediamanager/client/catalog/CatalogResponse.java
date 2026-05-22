package ps.emall.mediamanager.client.catalog;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.http.HttpStatus;
import ps.emall.mediamanager.common.response.ErrorCode;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CatalogResponse<T> {
    private List<ErrorCode> errorCodes;

    @JsonIgnore
    private HttpStatus status;

    private String message;
    private T data;

    public static <T> CatalogResponse<T> of(
            List<ErrorCode> errorCodes,
            HttpStatus status,
            String message,
            T data
    ) {
        return CatalogResponse.<T>builder()
                .errorCodes(errorCodes)
                .status(status)
                .message(message)
                .data(data)
                .build();
    }
}
