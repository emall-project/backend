package store.emall.backend.catalog.product.product_media;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;
import lombok.experimental.SuperBuilder;
import store.emall.backend.common.base.EMallsBaseDto;
import store.emall.backend.mediamanager.file.dto.FileDto;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ProductMediumDto extends EMallsBaseDto {
    @Null(message = "product.image.id.null")
    private Long id;

    @NotNull(message = "product.image.mediaId.notnull")
    private UUID mediumId;

    @NotNull(message = "product.image.sortOrder.notnull")
    private int sortOrder;

    private FileDto mediumFile;
}
