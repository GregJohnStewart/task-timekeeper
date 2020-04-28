package com.gjs.taskTimekeeper.webServer.server.toMoveToLib;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenCheckResponse {
    private boolean hadToken = false;
    private boolean tokenSecure = false;
    private boolean expired = true;
}
