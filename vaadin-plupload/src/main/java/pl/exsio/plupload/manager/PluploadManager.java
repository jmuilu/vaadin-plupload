/* 
 * The MIT License
 *
 * Copyright 2015 exsio.
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
package pl.exsio.plupload.manager;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.VerticalLayout;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import pl.exsio.plupload.Plupload;
import pl.exsio.plupload.PluploadFile;
import static pl.exsio.plupload.util.PluploadUtil.trimTextInTheMiddle;

/**
 *
 * @author exsio
 */
public class PluploadManager extends VerticalLayout {

    protected final HorizontalLayout controls;

    protected final VerticalLayout itemsContainer;

    protected final Map<String, Item> itemsMap;

    protected String removeLabel = "";

    protected final Plupload uploader = new Plupload("Browse", FontAwesome.FILES_O);

    protected final Button startButton = new Button("Start", FontAwesome.PLAY);

    protected final Button stopButton = new Button("Stop", FontAwesome.STOP);

    protected final Set<ItemCreationListener> itemCreationListeners = new LinkedHashSet<>();

    public PluploadManager() {
        this.controls = new HorizontalLayout();
        this.itemsContainer = new VerticalLayout();
        this.itemsMap = new LinkedHashMap();
        this.postConstruct();
    }

    private void postConstruct() {

        this.initManager();
        this.initControls();
        this.initItems();
        this.handleFilesAdded();
        this.handleFilesRemoved();
        this.handleUploadStart();
        this.handleUploadStop();
        this.handleUploadProgress();
        this.handleUploadComplete();
        this.handleStartButtonClick();
        this.handleStopButtonClick();
    }

    private void initManager() {
        this.setSpacing(true);
        this.setStyleName("plupload-mgr");
    }

    private void initControls() {
        this.controls.setSpacing(true);
        this.controls.setStyleName("plupload-mgr-controls");
        this.startButton.setEnabled(false);
        this.startButton.setStyleName("plupload-mgr-start");
        this.stopButton.setEnabled(false);
        this.stopButton.setStyleName("plupload-mgr-stop");
        this.controls.addComponent(uploader);
        this.controls.addComponent(startButton);
        this.controls.addComponent(stopButton);
        String id = "plupload-manager-" + this.uploader.getUploaderKey();
        this.setId(id);
        this.uploader.setStyleName("plupload-mgr-uploader");
        this.uploader.addDropZone(id);
        this.addComponent(this.controls);
    }

    private void initItems() {
        this.itemsContainer.setSpacing(true);
        this.itemsContainer.setStyleName("plupload-mgr-items");
        this.addComponent(this.itemsContainer);

    }

    /**
     * Returns an instance of Plupload class in order to reconfigure/ehnance it
     *
     * @return
     */
    public Plupload getUploader() {
        return this.uploader;
    }

    /**
     * Set the Start button label. Defaults to "Start"
     *
     * @param caption
     * @return
     */
    public PluploadManager setStartButtonCaption(String caption) {
        this.startButton.setCaption(caption);
        return this;
    }

    /**
     * Set the Browse button label. Defaults to "Browse"
     *
     * @param caption
     * @return
     */
    public PluploadManager setBrowseButtonCaption(String caption) {
        this.uploader.setCaption(caption);
        return this;
    }

    /**
     * Set the Stop button label. Defaults to "Stop"
     *
     * @param caption
     * @return
     */
    public PluploadManager setStopButtonCaption(String caption) {
        this.stopButton.setCaption(caption);
        return this;
    }

    /**
     * Set the Remove button label. Defaults to an empty String
     *
     * @param caption
     * @return
     */
    public PluploadManager setRemoveButtonCaption(String caption) {
        this.removeLabel = caption;
        for (String fileId : this.itemsMap.keySet()) {
            this.itemsMap.get(fileId).getRemoveButton().setCaption(caption);
        }
        return this;
    }

    /**
     * Get files uploaded by this manager
     *
     * @return
     */
    public PluploadFile[] getUploadedFiles() {
        return this.uploader.getUploadedFiles();
    }

    /**
     * Get the layout with Manager controls
     *
     * @return
     */
    public HorizontalLayout getControls() {
        return this.controls;
    }

    /**
     * Get
     *
     * @return the layout, that acts as a container for Items
     */
    public VerticalLayout getItemsContainer() {
        return this.itemsContainer;
    }

    /**
     *
     * Register an ItemCreationListener. It will enable you to customize an Item
     * before adding it to the container
     *
     * @param listener
     * @return
     */
    public PluploadManager addItemCreationListener(ItemCreationListener listener) {
        this.itemCreationListeners.add(listener);
        return this;
    }

    /**
     * Unregister an ItemCreationListener
     *
     * @param listener
     * @return
     */
    public PluploadManager removeItemCreationListener(ItemCreationListener listener) {
        this.itemCreationListeners.remove(listener);
        return this;
    }

    private void handleStopButtonClick() {
        this.stopButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                uploader.stop();
            }
        });
    }

    private void handleStartButtonClick() {
        this.startButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                uploader.start();
            }
        });
    }

    private void handleUploadComplete() {
        this.uploader.addUploadCompleteListener(new Plupload.UploadCompleteListener() {

            @Override
            public void onUploadComplete() {
                if (uploader.getQueuedFiles().length == 0) {
                    startButton.setEnabled(false);
                }
                stopButton.setEnabled(false);
            }
        });
    }

    private void handleUploadProgress() {
        this.uploader.addUploadProgressListener(new Plupload.UploadProgressListener() {

            @Override
            public void onUploadProgress(PluploadFile file) {
                if (itemsMap.containsKey(file.getId())) {
                    itemsMap.get(file.getId()).setProgress(file.getPercent());
                }
            }
        });
    }

    private void handleUploadStop() {
        this.uploader.addUploadStopListener(new Plupload.UploadStopListener() {

            @Override
            public void onUploadStop() {
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
            }
        });
    }

    private void handleUploadStart() {
        this.uploader.addUploadStartListener(new Plupload.UploadStartListener() {

            @Override
            public void onUploadStart() {
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
            }
        });
    }

    private void handleFilesRemoved() {
        this.uploader.addFilesRemovedListener(new Plupload.FilesRemovedListener() {

            @Override
            public void onFilesRemoved(PluploadFile[] files) {
                for (PluploadFile file : files) {
                    removeItem(file.getId());
                }
                toggleStartButton();
            }
        });
    }

    private void handleFilesAdded() {
        this.uploader.addFilesAddedListener(new Plupload.FilesAddedListener() {

            @Override
            public void onFilesAdded(PluploadFile[] files) {

                for (PluploadFile file : files) {
                    Item item = new Item(file);
                    for (ItemCreationListener listener : itemCreationListeners) {
                        listener.onCreateItem(item, file);
                    }
                    addItem(file.getId(), item);
                }
                toggleStartButton();
            }
        });
    }

    private void toggleStartButton() {
        startButton.setEnabled(uploader.getQueuedFiles().length > 0);
    }

    protected void addItem(String fileId, Item item) {
        this.itemsMap.put(fileId, item);
        this.itemsContainer.addComponent(item);
    }

    protected void removeItem(String fileId) {
        if (this.itemsMap.containsKey(fileId)) {
            this.itemsContainer.removeComponent(this.itemsMap.get(fileId));
            this.itemsMap.remove(fileId);
        }
    }

    /**
     * This will enable you to configure/customize an Item upon creation
     */
    public interface ItemCreationListener {

        void onCreateItem(Item item, PluploadFile file);

    }

    /**
     * This is a single Manager item, which is an UI representation of a File
     * being added to the queue
     */
    public class Item extends HorizontalLayout {

        protected ProgressBar progressBar;

        protected Label nameLabel;

        protected Label percentLabel;

        protected Button removeButton;

        public Item(final PluploadFile file) {

            this.setSpacing(true);
            this.progressBar = new ProgressBar();
            this.progressBar.setIndeterminate(false);
            this.progressBar.setValue(0f);
            this.progressBar.setWidth("270px");
            this.progressBar.setStyleName("plupload-mgr-item-progressbar");

            this.setStyleName("plupload-mgr-item plupload-mgr-item-" + file.getId());

            this.nameLabel = new Label(trimTextInTheMiddle(file.getName(), 30));
            this.nameLabel.setWidth("270px");
            this.nameLabel.setDescription(file.getName());
            this.nameLabel.setStyleName("plupload-mgr-item-name");

            this.percentLabel = new Label("0%");
            this.percentLabel.setWidth("40px");
            this.percentLabel.setStyleName("plupload-mgr-item-percent");

            this.removeButton = new Button(removeLabel, FontAwesome.TIMES);
            this.removeButton.setStyleName("plupload-mgr-item-remove");
            this.removeButton.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    uploader.removeFile(file.getId());
                }
            });

            VerticalLayout vlayout = new VerticalLayout();
            vlayout.setSpacing(false);
            vlayout.addComponent(this.nameLabel);
            vlayout.addComponent(this.progressBar);

            this.addComponent(vlayout);
            this.addComponent(this.percentLabel);
            this.addComponent(this.removeButton);
            this.setComponentAlignment(this.percentLabel, Alignment.MIDDLE_CENTER);
            this.setComponentAlignment(this.removeButton, Alignment.MIDDLE_RIGHT);

        }

        public void setProgress(long percent) {
            this.progressBar.setValue(new Long(percent).floatValue() / 100);
            this.percentLabel.setValue(percent + "%");
        }

        public ProgressBar getProgressBar() {
            return progressBar;
        }

        public Label getNameLabel() {
            return nameLabel;
        }

        public Label getPercentLabel() {
            return percentLabel;
        }

        public Button getRemoveButton() {
            return removeButton;
        }

    }
}
