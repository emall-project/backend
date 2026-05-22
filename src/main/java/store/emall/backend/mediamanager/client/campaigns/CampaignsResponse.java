package ps.emall.mediamanager.client.campaigns;

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
public class CampaignsResponse <T> {
    private List<ErrorCode> errorCodes;

    @JsonIgnore
    private HttpStatus status;

    private String message;
    private T data;

    public static <T> CampaignsResponse<T> of(
            List<ErrorCode> errorCodes,
            HttpStatus status,
            String message,
            T data
    ) {
        return CampaignsResponse.<T>builder()
                .errorCodes(errorCodes)
                .status(status)
                .message(message)
                .data(data)
                .build();
    }
}
