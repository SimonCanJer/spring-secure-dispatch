package com.secured.api.data;

public interface  ICachedUserDataAccess extends ICachedUserDetailAccess {
    ImpersonatedDataShare getImpersonationDataSharing();
}
