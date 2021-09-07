package  com.coding.challenge.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Custom application event to inform the user as to the progress
 * of their uploaded file.
 */
@Getter
public class UploadProgressEvent extends ApplicationEvent {

    private final Boolean complete;
    private final String requestId;

    public UploadProgressEvent(String requestId, Boolean complete, String message) {
        super(message);
        this.requestId = requestId;
        this.complete = complete;
    }
}