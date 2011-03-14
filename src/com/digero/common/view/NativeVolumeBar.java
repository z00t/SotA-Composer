package com.digero.common.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;

public class NativeVolumeBar extends JPanel {
	private static final int PTR_WIDTH = 12;
	private static final int PTR_HEIGHT = 12;
	private static final int BAR_HEIGHT = 6;
	private static final int SIDE_PAD = PTR_WIDTH / 2;
	private static final int ROUND = 6;

	public static final int WIDTH = PTR_WIDTH * 5;

	public interface NativeCallback {
		float getVolume();

		void setVolume(float volume);
	}

	private final int MAX_VOLUME = 127;
	private NativeCallback callback;

	private Rectangle ptrRect = new Rectangle(0, 0, PTR_WIDTH, PTR_HEIGHT);

	public NativeVolumeBar(NativeCallback callback) {
		this.callback = callback;

		MouseHandler mouseHandler = new MouseHandler();
		addMouseListener(mouseHandler);
		addMouseMotionListener(mouseHandler);

		Dimension sz = new Dimension(WIDTH, PTR_HEIGHT);
		setMinimumSize(sz);
		setPreferredSize(sz);
		updatePointerRect();
	}

	private float getVolume() {
		return callback.getVolume() * MAX_VOLUME;
	}

	private void setVolume(float volume) {
		callback.setVolume(volume / MAX_VOLUME);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int ptrPos = (int) (SIDE_PAD + (getWidth() - 2 * SIDE_PAD) * getVolume() / MAX_VOLUME);

		final int x = 0;
		final int y = (PTR_HEIGHT - BAR_HEIGHT) / 2;
		int right = getWidth();

		g2.setClip(new RoundRectangle2D.Float(x, y, right - x, BAR_HEIGHT, ROUND, ROUND));
		g2.setPaint(new GradientPaint(0, y, Color.DARK_GRAY, 0, y + BAR_HEIGHT, Color.GRAY));
		g2.fillRect(x, y, ptrPos - x, BAR_HEIGHT);

		g2.setPaint(new GradientPaint(0, y, Color.LIGHT_GRAY, 0, y + BAR_HEIGHT, Color.WHITE));
		g2.fillRect(ptrPos, y, right - ptrPos, BAR_HEIGHT);
		g2.setClip(null);

		g2.setColor(Color.BLACK);
		g2.drawRoundRect(x, y, right - x - 1, BAR_HEIGHT, ROUND, ROUND);

		// Pointer
		int left = ptrPos - PTR_WIDTH / 2;

		final Color PTR_COLOR_1 = Color.WHITE;
		final Color PTR_COLOR_2 = Color.LIGHT_GRAY;

		g2.setPaint(new GradientPaint(left, 0, PTR_COLOR_1, left + PTR_WIDTH, 0, PTR_COLOR_2));
		g2.fillOval(left, 0, PTR_WIDTH, PTR_HEIGHT);
		g2.setColor(Color.BLACK);
		g2.drawOval(left, 0, PTR_WIDTH - 1, PTR_HEIGHT - 1);
	}

	private void updatePointerRect() {
		ptrRect.x = (int) (getWidth() * getVolume() / MAX_VOLUME - PTR_WIDTH / 2);
	}

	private class MouseHandler implements MouseListener, MouseMotionListener {
		private int getPosition(int x) {
			int pos = (int) ((x + 1 - SIDE_PAD) * MAX_VOLUME / (getWidth() - 2 * SIDE_PAD));
			if (pos < 0) {
				pos = 0;
			}
			if (pos > MAX_VOLUME) {
				pos = (int) MAX_VOLUME;
			}
			return pos;
		}

		public void mouseClicked(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			if (!NativeVolumeBar.this.isEnabled())
				return;
			setVolume(getPosition(e.getX()));
			repaint();
			requestFocus();
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseDragged(MouseEvent e) {
			if (!NativeVolumeBar.this.isEnabled())
				return;
			setVolume(getPosition(e.getX()));
			repaint();
		}

		public void mouseMoved(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}
	}
}
