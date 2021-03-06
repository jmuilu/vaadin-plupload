/*
 * The MIT License
 *
 * Copyright 2015 sdymi_000.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package pl.exsio.plupload.handler.file;

import pl.exsio.plupload.PluploadFile;
import pl.exsio.plupload.handler.PluploadChunkHandler;
import pl.exsio.plupload.handler.PluploadChunkHandlerFactory;

/**
 *
 * @author eXsio
 */
public class FileAppendingChunkHandlerFactory implements PluploadChunkHandlerFactory {

    protected String uploadPath;

    public FileAppendingChunkHandlerFactory(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    @Override
    public PluploadChunkHandler create(PluploadFile file) {
        return new FileAppendingChunkHandler(this.uploadPath);
    }

    public String getUploadPath() {
        return uploadPath;
    }

}
