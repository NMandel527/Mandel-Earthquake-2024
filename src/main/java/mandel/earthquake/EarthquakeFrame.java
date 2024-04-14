package mandel.earthquake;

import hu.akarnokd.rxjava3.swing.SwingSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import mandel.earthquake.json.Feature;
import mandel.earthquake.json.FeatureCollection;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicListUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class EarthquakeFrame extends JFrame {

    private final JList<String> earthquakeList = new JList<>();
    private final JRadioButton hourButton;
    private final JRadioButton monthButton;
    private FeatureCollection currentResponse;

    public EarthquakeFrame() {

        setTitle("EarthquakeFrame");
        setSize(300, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        hourButton = new JRadioButton("One Hour");
        hourButton.setActionCommand("One Hour");
        hourButton.setSelected(true);

        monthButton = new JRadioButton("30 Days");
        monthButton.setActionCommand("30 Days");

        ButtonGroup group = new ButtonGroup();
        group.add(hourButton);
        group.add(monthButton);

        JPanel radioButtonPanel = new JPanel();
        radioButtonPanel.add(hourButton);
        radioButtonPanel.add(monthButton);

        add(radioButtonPanel, BorderLayout.NORTH);

        EarthquakeService service = new EarthquakeServiceFactory().getService();

        hourButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (hourButton.isSelected()) {
                    Disposable disposable = service.oneHour()
                            // tells Rx to request the data on a background Thread
                            .subscribeOn(Schedulers.io())
                            // tells Rx to handle the response on Swing's main Thread
                            .observeOn(SwingSchedulers.edt())
                            //.observeOn(AndroidSchedulers.mainThread()) // Instead use this on Android only
                            .subscribe(
                                    (response) -> { currentResponse = response;
                                        handleResponse(response); },
                                    Throwable::printStackTrace);
                }
            }
        });

        monthButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (monthButton.isSelected()) {
                    Disposable disposable2 = service.oneMonth()
                            // tells Rx to request the data on a background Thread
                            .subscribeOn(Schedulers.io())
                            // tells Rx to handle the response on Swing's main Thread
                            .observeOn(SwingSchedulers.edt())
                            //.observeOn(AndroidSchedulers.mainThread()) // Instead use this on Android only
                            .subscribe(
                                    (response) -> { currentResponse = response;
                                        handleResponse(response); },
                                    Throwable::printStackTrace);
                }
            }
        });

        ListSelectionModel listSelectionModel = earthquakeList.getSelectionModel();
        listSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Feature feature = currentResponse.features[earthquakeList.getSelectedIndex()];
                double lng = feature.geometry.coordinates[0];
                double lat = feature.geometry.coordinates[1];
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        Desktop.getDesktop().browse(
                                new URI("https://www.google.com/maps/search/?api=1&query=" + lat + "," + lng));
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        add(new JScrollPane(earthquakeList), BorderLayout.CENTER);
    }

    private void handleResponse(FeatureCollection response) {

        String[] listData = new String[response.features.length];
        for (int i = 0; i < response.features.length; i++) {
            Feature feature = response.features[i];
            listData[i] = feature.properties.mag + " " + feature.properties.place;
        }
        earthquakeList.setListData(listData);
    }
}
