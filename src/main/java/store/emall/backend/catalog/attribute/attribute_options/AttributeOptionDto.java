package store.emall.backend.catalog.attribute.attribute_options;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import store.emall.backend.catalog.attribute.AttributeType;
import store.emall.backend.common.base.EMallsBaseDto;
import store.emall.backend.common.validation.OnCreate;
import store.emall.backend.common.validation.OnUpdate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AttributeOptionDto extends EMallsBaseDto {
    @Null(message = "attribute.option.id.null")
    private Long id;

    @NotBlank(message = "attribute.option.value.notblank")
    @Size(min = 3, max = 50, message = "attribute.option.value.size")
    private String value;

    @NotNull(message = "attribute.option.sortOrder.notnull")
    private int sortOrder;

}
