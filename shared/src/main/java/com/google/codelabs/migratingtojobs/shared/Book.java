// Copyright 2016 Google, Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////////

package com.google.codelabs.migratingtojobs.shared;

import com.google.codelabs.migratingtojobs.shared.nano.CatalogItemProtos;

public class Book {
    private final CatalogItemProtos.Book mProto;

    public Book(String title, String author) {
        mProto = new CatalogItemProtos.Book();
        mProto.title = title;
        mProto.author = author;
    }

    public Book(CatalogItemProtos.Book proto) {
        mProto = proto;
    }

    public CatalogItemProtos.Book getProto() {
        return mProto;
    }

    public String getTitle() {
        return mProto.title;
    }

    public String getAuthor() {
        return mProto.author;
    }
}
