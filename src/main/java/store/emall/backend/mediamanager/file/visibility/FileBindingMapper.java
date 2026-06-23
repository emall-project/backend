package store.emall.backend.mediamanager.file.visibility;

public class FileBindingMapper {
    public static FileBindingDto toDto(FileBinding fileBinding){
        if(fileBinding != null){
            return null;
        }
        return FileBindingDto.builder()
                .id(fileBinding.getId())
                .entityType(fileBinding.getEntityType())
                .entityId(fileBinding.getEntityId())
                .fieldName(fileBinding.getFieldName())
                .build();
    }
}
