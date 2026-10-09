---
name: Sovereign Slate
colors:
  surface: '#0b1326'
  surface-dim: '#0b1326'
  surface-bright: '#31394d'
  surface-container-lowest: '#060e20'
  surface-container-low: '#131b2e'
  surface-container: '#171f33'
  surface-container-high: '#222a3d'
  surface-container-highest: '#2d3449'
  on-surface: '#dae2fd'
  on-surface-variant: '#c3c6d7'
  inverse-surface: '#dae2fd'
  inverse-on-surface: '#283044'
  outline: '#8d90a0'
  outline-variant: '#434655'
  surface-tint: '#b4c5ff'
  primary: '#b4c5ff'
  on-primary: '#002a78'
  primary-container: '#2563eb'
  on-primary-container: '#eeefff'
  inverse-primary: '#0053db'
  secondary: '#4edea3'
  on-secondary: '#003824'
  secondary-container: '#00a572'
  on-secondary-container: '#00311f'
  tertiary: '#ffb3ad'
  on-tertiary: '#68000a'
  tertiary-container: '#cf2c30'
  on-tertiary-container: '#ffecea'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#dbe1ff'
  primary-fixed-dim: '#b4c5ff'
  on-primary-fixed: '#00174b'
  on-primary-fixed-variant: '#003ea8'
  secondary-fixed: '#6ffbbe'
  secondary-fixed-dim: '#4edea3'
  on-secondary-fixed: '#002113'
  on-secondary-fixed-variant: '#005236'
  tertiary-fixed: '#ffdad7'
  tertiary-fixed-dim: '#ffb3ad'
  on-tertiary-fixed: '#410004'
  on-tertiary-fixed-variant: '#930013'
  background: '#0b1326'
  on-background: '#dae2fd'
  surface-variant: '#2d3449'
typography:
  display-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 36px
    fontWeight: '700'
    lineHeight: 44px
    letterSpacing: -0.02em
  display-lg-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 36px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
    letterSpacing: -0.015em
  headline-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 26px
    letterSpacing: -0.01em
  title-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 24px
    letterSpacing: -0.005em
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
    letterSpacing: 0em
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
    letterSpacing: 0em
  metric-tabular:
    fontFamily: Inter
    fontSize: 15px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: -0.01em
  label-md:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.01em
  label-sm:
    fontFamily: Inter
    fontSize: 11px
    fontWeight: '600'
    lineHeight: 14px
    letterSpacing: 0.04em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  gutter-sm: 0.75rem
  margin: 1.25rem
  margin-tablet: 2rem
  margin-desktop: 3rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style
The design system projects mathematical rigor, quiet luxury, and institutional trust tailored for discerning personal wealth management. It targets modern professionals, investors, and high-net-worth individuals requiring immediate clarity over multi-asset portfolios and daily cash flows. 

The aesthetic is Modern Fintech Precision: a discipline combining the utilitarian speed of high-density financial terminals with the tactile polish of modern mobile interfaces. The experience rejects ornamental distraction in favor of structured data, micro-feedback, confident contrast, and crisp hierarchy. The UI evokes financial clarity, disciplined control, and absolute security.

## Colors
The color foundation utilizes an immersive, low-strain dark mode canvas structured around deep navy slate hues.

- **Canvas & Structural Layers**: Base application canvas rests on `#0F172A` (Slate 900), layered with `#1E293B` (Slate 800) for structural card containers and interactive surface tiles, framed by subtle micro-borders of `#334155` (Slate 700) at 50% opacity.
- **Brand Primary (`#2563EB`)**: An electric royal blue reserved for high-priority user actions, active states, key data trends, and identity touchpoints.
- **Directional Semantic Pairs**:
  - Positive Cashflow & Growth: `#10B981` (Emerald). Indicates yield, income streams, and upward vector deltas.
  - Outflow & Risk Thresholds: `#EF4444` (Vibrant Coral Red). Reserved for expense debits, drawdown metrics, and system-critical security alerts.
- **Data Visualizations & Accents**: Supporting auxiliary data sets (such as category distribution or secondary asset classes) employ calibrated tertiary slates (`#94A3B8`) and an alternate cyan indigo (`#38BDF8`).

## Typography
The system enforces a dual-type architecture. Plus Jakarta Sans handles high-level display values, asset totals, and section headings to inject a modern, refined tone. Inter is utilized for dense information layers, tabular transaction ledgers, body content, and operational tags.

All numbers displaying currency, net worth, rates of return, and ticker valuations must enable `font-feature-settings: "tnum" on, "cv05" on` (tabular figures and adjusted punctuation) to prevent layout shifts during live ticker animations and ensure absolute vertical optical alignment across financial columns.

## Layout & Spacing
The layout model is mobile-first, operating on an 8pt base grid with a 4pt sub-grid for micro-alignments, badge insets, and icon bounds.

- **Mobile Viewports (<640px)**: The content conforms to a single-column stacked fluid layout with fixed `1.25rem` (20px) horizontal page margins and `0.75rem` (12px) component spacing gutters. Safe areas are maintained for edge-to-edge navigation and biometric interaction targets.
- **Tablet & Split-Panels (640px–1024px)**: Transitions to a 6-column fluid structure with `1rem` (16px) gutters and `2rem` margins, enabling balance cards and portfolio charts to sit side-by-side with recent activities.
- **Desktop/Terminal Viewports (>1024px)**: Adapts into a structured 12-column dashboard capped at `1280px` maximum layout width, standardizing a `1rem` gutter and `3rem` canvas margins.

## Elevation & Depth
Depth is constructed through ambient tonal surface layering combined with soft, directional luminosity rather than dark, heavy drop shadows:

- **Level 0 (Canvas Base)**: Pure `#0F172A`. Un-elevated application background.
- **Level 1 (Card & Module Layer)**: `#1E293B` with a continuous 1px hairline border in `rgba(255, 255, 255, 0.08)`. Subtle ambient shadow: `0 4px 20px -2px rgba(0, 0, 0, 0.35)`.
- **Level 2 (Active Modals, Quick Actions & Menus)**: `#283548` with a 1px border in `rgba(255, 255, 255, 0.12)`. Cast shadow: `0 12px 32px -4px rgba(0, 0, 0, 0.5)`.
- **Level 3 (Tactile Hover & Drag States)**: Elevates with an energetic back-tinted glow using the primary brand blue: `0 0 24px -2px rgba(37, 99, 235, 0.25)`.
- **Surface Sheen**: High-priority cards (such as the Primary Total Wealth Card) feature an angled, low-opacity radial highlight from the top-left edge: `radial-gradient(ellipse at top left, rgba(37, 99, 235, 0.15), transparent 70%)`.

## Shapes
The visual form factor favors a rounded, contemporary profile that counters technical stiffness with warmth and handling ergonomics.

- Standard UI cards, modular containers, and sheets utilize `rounded-lg` (`1rem` / 16px).
- Embedded elements within containers (e.g., list item rows, chart tooltips, sub-panels) scale down to default `rounded` (`0.5rem` / 8px).
- Badges, directional metrics, and quick-action icon containers adopt full circular or pill radii (`9999px`) to maintain distinct visual semantics from content containers.

## Components

### Buttons
- **Primary**: Background `#2563EB`, text `#FFFFFF`, font weight `600`, height `48px`, shape `rounded-lg`. High-luminosity hover state shifts to `#1D4ED8`. Focus state presents a `2px` offset ring in `#38BDF8`.
- **Secondary / Ghost**: Background `rgba(30, 41, 59, 0.7)`, border `1px solid rgba(255, 255, 255, 0.1)`, text `#F8FAFC`.
- **Destructive**: Background `rgba(239, 68, 68, 0.12)`, text `#EF4444`, border `1px solid rgba(239, 68, 68, 0.3)`.

### Cards & Quick-Action Modules
- **Overview Wealth Card**: Styled with a deep slate gradient, a 1px edge stroke of `rgba(255, 255, 255, 0.12)`, padding of `space-lg`, featuring total net balance in `display-lg`, flanked by a delta metric pill.
- **Quick-Action Utilities**: 4-column icon-over-label matrix. Square rounded surfaces (`rounded-lg`, `#1E293B`), featuring centered `24px` vector symbols with active press-scale micro-interactions (scale `0.97`).

### Chips & Financial Metric Badges
- Strict pill morphology (`rounded-full`) with `space-xs` vertical and `space-sm` horizontal padding.
- **Income/Positive Tag**: Background `rgba(16, 185, 129, 0.15)`, text `#10B981`, leading up-right arrow icon (`↗`).
- **Expense/Negative Tag**: Background `rgba(239, 68, 68, 0.15)`, text `#EF4444`, leading down-right arrow icon (`↘`).
- **Neutral Filter Chips**: Background `#1E293B`, border `1px solid #334155`, text `#94A3B8`. Active filter turns to `#2563EB` fill with white text.

### Transaction Lists
- Row items separated by zero-line dividers; separation relies on `0.5rem` vertical spacing gaps.
- Icon avatar on the left (`40px`, `rounded-full`, categorical slate background), middle vertical lockup containing Payee Name (`title-md`) and Category timestamp (`label-md`), right-aligned tabular amount formatted via `metric-tabular`.

### Input Fields & Controls
- **Inputs**: Dark field surface (`#0F172A`), container border `1px solid #334155`, text `#F8FAFC`, placeholder `#64748B`. Focused state engages `#2563EB` 1px border with a soft ambient blue glow ring.
- **Checkboxes & Radios**: Size `20px`, surface `#1E293B`, selected state `#2563EB` containing a crisp white vector checkmark.

### Bar Charts & Micro-Visualizations
- Vertical cashflow bars set against horizontal grid lines styled with `#334155` at 30% opacity.
- Income bars render with `#10B981`, expense bars with `#EF4444`. Rounded tops only (`rounded-t-[4px]`).
- Interactive pointer activates a floating tooltip card: `#0F172A` background, `1px solid #334155` border, display metric in tabular bold.