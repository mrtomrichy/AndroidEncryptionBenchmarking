package com.mrtomrichy.encryptionbenchmarking.ui;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mrtomrichy.encryptionbenchmarking.R;
import com.mrtomrichy.encryptionbenchmarking.adapter.AlgorithmAdapter;
import com.mrtomrichy.encryptionbenchmarking.encryption.Algorithm;
import com.mrtomrichy.encryptionbenchmarking.encryption.Encryption;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private FloatingActionButton fab;
  private RelativeLayout layout;
  private float maxTranslationY;
  private boolean isFabHidden = false;
  private boolean isCurrentlySelecting = false;
  private AlgorithmAdapter adapter;

  private ArrayList<AlgorithmAdapter.AlgorithmListModel> algorithmList;

  private Menu menu;
  private boolean customMenuVisible = true;
  private TextView customMenuText;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);


    algorithmList = new ArrayList<>();

    for(Algorithm algorithm : Encryption.getSupportedEncryptionTypes()) {
      algorithmList.add(new AlgorithmAdapter.AlgorithmListModel(algorithm));
    }

    RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.algorithmList);
    mRecyclerView.setHasFixedSize(true);

    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
    mRecyclerView.setLayoutManager(mLayoutManager);

    adapter = new AlgorithmAdapter(algorithmList, this);

    adapter.setAlgorithmSelectionListener(new AlgorithmAdapter.AlgorithmSelectionListener() {
      @Override
      public void onAlgorithmPressed(AlgorithmAdapter.AlgorithmListModel item) {
        if(isCurrentlySelecting) {
          item.selected = !item.selected;
          adapter.notifyDataSetChanged();
          updateUI(true);
        } else {
          List<Algorithm> algorithm = new ArrayList<>();
          algorithm.add(item.algorithm);
          goToBenchmarks(algorithm);
        }
      }

      @Override
      public void onAlgorithmLongPressed(AlgorithmAdapter.AlgorithmListModel item) {
        if(!isCurrentlySelecting) {
          isCurrentlySelecting = true;
          item.selected = !item.selected;
          adapter.notifyDataSetChanged();
          updateUI(true);
        } else {
          item.selected = !item.selected;
          adapter.notifyDataSetChanged();
          updateUI(true);
        }
      }
    });

    mRecyclerView.setAdapter(adapter);

    fab = (FloatingActionButton) findViewById(R.id.benchmarkButton);
    layout = (RelativeLayout) findViewById(R.id.mainActivityLayout);

    layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        maxTranslationY = (layout.getHeight() - fab.getY()) + fab.getHeight();
        updateUI(false);
      }
    });

    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        goToBenchmarks(adapter.getSelectedAlgorithms());
      }
    });
  }

  private void goToBenchmarks(List<Algorithm> selectedAlgorithms) {
    Algorithm[] algos = new Algorithm[selectedAlgorithms.size()];
    selectedAlgorithms.toArray(algos);

    Bundle b = new Bundle();
    b.putParcelableArray(BenchmarkActivity.ALGORITHM_TAG, algos);

    Intent i = new Intent(MainActivity.this, BenchmarkActivity.class);
    i.putExtras(b);
    startActivity(i);
  }

  private void selectAll() {
    for(AlgorithmAdapter.AlgorithmListModel algo : algorithmList) {
      algo.selected = true;
    }

    adapter.notifyDataSetChanged();
    updateUI(true);
  }

  private void deselectAll() {
    for(AlgorithmAdapter.AlgorithmListModel algo : algorithmList) {
      algo.selected = false;
    }

    adapter.notifyDataSetChanged();
    updateUI(true);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    this.menu = menu;
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_main, menu);
    customMenuVisible = true;
    updateUI(true);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_select_all:
        selectAll();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void updateUI(boolean shouldAnimate) {
    int algorithmSelectedCount = adapter.getSelectedCount();
    if(algorithmSelectedCount == 0) {
      isCurrentlySelecting = false;
      updateMenu(false);

      if(!isFabHidden) {
        hideFAB(shouldAnimate);
      }
    } else {
      updateMenu(true);
      if(isFabHidden) {
        showFAB(shouldAnimate);
      }
    }
  }

  private void updateMenu(boolean showCustom) {
    if(showCustom && !customMenuVisible) {
      showSelectAll();
      showCustomMenu();
    } else if(!showCustom && customMenuVisible) {
      hideSelectAll();
      hideCustomMenu();
    }

    customMenuVisible = showCustom;

    if(customMenuVisible) {
      customMenuText.setText(String.format(getString(R.string.total_selected_algorithm_count), adapter.getSelectedCount(), adapter.getItemCount()));
    }
  }

  private void showCustomMenu() {
    ActionBar ab = getSupportActionBar();
    View v = LayoutInflater.from(this).inflate(R.layout.deselect_menu, null, false);
    ImageButton tick = (ImageButton) v.findViewById(R.id.doneButton);
    customMenuText = (TextView) v.findViewById(R.id.selectedText);

    ab.setCustomView(v);
    ab.setDisplayShowCustomEnabled(true);
    ab.setDisplayShowTitleEnabled(false);

    tick.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        deselectAll();
      }
    });
  }

  private void hideCustomMenu() {
    ActionBar ab = getSupportActionBar();
    ab.setDisplayShowCustomEnabled(false);
    ab.setDisplayShowTitleEnabled(true);
  }

  private void showSelectAll() {
    if(menu == null) return;

    MenuItem item = menu.findItem(R.id.action_select_all);
    item.setVisible(true);
  }

  private void hideSelectAll() {
    if(menu == null) return;

    MenuItem item = menu.findItem(R.id.action_select_all);
    item.setVisible(false);
  }

  private void hideFAB(boolean shouldAnimate) {
    if(shouldAnimate) animateFAB(fab.getTranslationY(), maxTranslationY);
    else              fab.setTranslationY(maxTranslationY);
    isFabHidden = true;
  }

  private void showFAB(boolean shouldAnimate) {
    if(shouldAnimate) animateFAB(fab.getTranslationY(), 0);
    else              fab.setTranslationY(0);

    isFabHidden = false;
  }

  private void animateFAB(float start, float end) {
    ObjectAnimator animator = ObjectAnimator.ofFloat(fab, View.TRANSLATION_Y, start, end);
    animator.setInterpolator(new DecelerateInterpolator());
    animator.setDuration(200);
    animator.start();
  }

  @Override
  public void onBackPressed() {
    if(!isCurrentlySelecting) {
      super.onBackPressed();
    } else {
      deselectAll();
    }
  }
}
