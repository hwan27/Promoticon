package com.promoticon.settings.form;

import com.promoticon.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Notifications {

    private boolean emoticonCreatedByWeb;

    private boolean emoticonCreatedByEmail;

    private boolean feedbackUpdatedByWeb;

    private boolean feedbackUpdatedByEmail;


}
