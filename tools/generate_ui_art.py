import math
import os
import random
from PIL import Image, ImageDraw, ImageFilter

OUT = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))),
                   "src", "main", "resources", "images")
os.makedirs(OUT, exist_ok=True)
random.seed(11)


def lerp(a, b, t):
    return tuple(int(round(a[i] + (b[i] - a[i]) * t)) for i in range(3))


def diagonal(size, start, end):
    small = Image.new("RGB", (256, 256))
    px = small.load()
    for y in range(256):
        for x in range(256):
            px[x, y] = lerp(start, end, (x / 255.0 * 0.55) + (y / 255.0 * 0.45))
    return small.resize(size, Image.BICUBIC)


def glow(img, center, radius, color, strength):
    layer = Image.new("RGBA", img.size, (0, 0, 0, 0))
    draw = ImageDraw.Draw(layer)
    cx, cy = center
    steps = 24
    for i in range(steps, 0, -1):
        r = radius * i / steps
        alpha = int(strength * (1 - i / steps) ** 1.9)
        draw.ellipse([cx - r, cy - r, cx + r, cy + r], fill=color + (alpha,))
    layer = layer.filter(ImageFilter.GaussianBlur(radius / 10))
    return Image.alpha_composite(img.convert("RGBA"), layer)


def frond(draw, base, angle, length, width, droop, color):
    tx, ty = base
    left, right = [], []
    steps = 12
    for i in range(steps + 1):
        t = i / steps
        x = tx + math.cos(angle) * length * t
        y = ty + math.sin(angle) * length * t + droop * (t ** 2)
        w = width * (1 - t) ** 0.65 + 0.6
        nx, ny = -math.sin(angle), math.cos(angle)
        left.append((x + nx * w, y + ny * w))
        right.append((x - nx * w, y - ny * w))
    draw.polygon(left + right[::-1], fill=color)


def palm(draw, x, baseline, scale, color):
    tx, ty = x + 16 * scale, baseline - 168 * scale
    draw.line([(x, baseline), (x - 8 * scale, ty + 96 * scale)], fill=color, width=max(3, int(9 * scale)))
    draw.line([(x - 8 * scale, ty + 96 * scale), (tx, ty)], fill=color, width=max(2, int(7 * scale)))
    for i in range(7):
        angle = -math.pi * (0.06 + 0.88 * i / 6) + (random.random() - 0.5) * 0.12
        frond(draw, (tx, ty), angle, (86 + random.random() * 26) * scale, 9 * scale, 26 * scale, color)
    draw.ellipse([tx - 12 * scale, ty - 10 * scale, tx + 12 * scale, ty + 10 * scale], fill=color)


def cover(path):
    w, h = 760, 980
    img = glow(diagonal((w, h), (7, 43, 58), (14, 106, 92)), (int(w * 0.72), int(h * 0.24)), int(w * 0.42), (255, 241, 196), 92)
    draw = ImageDraw.Draw(img)
    draw.polygon([(0, h)] + [(x, h * 0.66 + 22 * math.sin(x / w * 5.1)) for x in range(0, w + 8, 8)] + [(w, h)], fill=(9, 59, 62))
    draw.polygon([(0, h)] + [(x, h * 0.74 + 18 * math.sin(x / w * 4.2 + 2)) for x in range(0, w + 8, 8)] + [(w, h)], fill=(8, 74, 68))
    draw.polygon([(0, h)] + [(x, h * 0.82 + 14 * math.sin(x / w * 3.1 + 4)) for x in range(0, w + 8, 8)] + [(w, h)], fill=(6, 61, 55))
    for x, s in ((70, 1.05), (250, 0.8), (470, 1.15), (660, 0.92)):
        palm(draw, x, int(h * 0.82 + random.randint(-16, 16)), s, (4, 34, 32))
    draw.rectangle([0, int(h * 0.82), w, h], fill=(4, 30, 32))
    veil = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    vd = ImageDraw.Draw(veil)
    for y in range(h):
        t = y / h
        alpha = int(130 * max(0.0, t - 0.52) / 0.48)
        vd.line([(0, y), (w, y)], fill=(2, 20, 24, alpha))
    Image.alpha_composite(img.convert("RGBA"), veil).convert("RGB").save(path, quality=94)


def logo(size=256):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    grad = diagonal((size, size), (18, 125, 110), (21, 94, 239))
    mask = Image.new("L", (size, size), 0)
    ImageDraw.Draw(mask).rounded_rectangle([0, 0, size - 1, size - 1], radius=int(size * 0.26), fill=255)
    img.paste(grad, (0, 0), mask)
    draw = ImageDraw.Draw(img)
    cx, cy = size * 0.5, size * 0.62
    draw.line([(cx, cy), (cx + size * 0.02, cy - size * 0.26)], fill=(255, 255, 255), width=int(size * 0.035))
    for dx, dy in ((-1, 0.12), (-0.72, -0.5), (-0.26, -0.86), (0.26, -0.86), (0.72, -0.5), (1, 0.12)):
        draw.line([(cx, cy - size * 0.26), (cx + dx * size * 0.19, cy - size * 0.26 + dy * size * 0.17)],
                  fill=(255, 255, 255), width=int(size * 0.032))
    draw.arc([cx - size * 0.30, cy - size * 0.06, cx + size * 0.30, cy + size * 0.24], 200, 340,
             fill=(255, 255, 255), width=int(size * 0.03))
    img.save(os.path.join(OUT, "logo.png"))


def header(path):
    w, h = 1400, 200
    img = glow(diagonal((w, h), (10, 61, 78), (16, 118, 100)), (int(w * 0.82), 0), int(w * 0.2), (255, 246, 214), 58)
    draw = ImageDraw.Draw(img)
    draw.polygon([(0, h)] + [(x, h * 0.72 + 12 * math.sin(x / w * 4.7)) for x in range(0, w + 8, 8)] + [(w, h)], fill=(8, 84, 76))
    for x, s in ((1010, 0.55), (1150, 0.45), (1290, 0.6)):
        palm(draw, x, int(h * 1.05), s, (5, 48, 45))
    img.convert("RGB").save(path, quality=94)


cover(os.path.join(OUT, "cover-login.png"))
logo()
header(os.path.join(OUT, "header-bg.png"))
print("generated", sorted(os.listdir(OUT)))
